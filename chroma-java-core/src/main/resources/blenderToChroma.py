# 4
# version number needs to be incremented each time the file changes!
import math
import mathutils
import struct
import bpy
import json

writePackedBinary = True

filepathPRFX = bpy.data.filepath.rsplit('.', 1)[0]

meshfile = open(filepathPRFX + ".mesh.bin", "wb") if writePackedBinary else open(filepathPRFX + ".mesh.json", "w")
matfile = open(filepathPRFX + ".mat.json", "w")
camfile = open(filepathPRFX + ".cam.json", "w")

objList = bpy.data.objects
matList = bpy.data.materials
scene = bpy.context.scene

materialDict = {}
matIdx = 0
materialExport = []
for mat in matList:
    print('Exporting material: ' + mat.name)
    material = {}
    material["name"] = mat.name
    material["specHardness"] = mat.specular_hardness
    material["emits"] = mat.emit
    material["ior"] = mat.raytrace_transparency.ior
    materialType = "NULL"

    if mat.use_transparency is True:
        materialType = "GLASS"
        # small hack because there is no definition for absorbtion color for dielectrics
        material["color"] = [mat.diffuse_color[0], mat.diffuse_color[1], mat.diffuse_color[2]]

    if mat.specular_intensity > 0 or mat.specular_hardness > 1:
        if materialType is not "NULL":
            print("WARNING: Non-unique material definition! Was [" + materialType + "], gets [PLASTIC]!")
        materialType = "PLASTIC"
        material["color"] = [mat.diffuse_color[0], mat.diffuse_color[1], mat.diffuse_color[2]]

    if mat.raytrace_mirror.use is True:
        if materialType is not "NULL":
            print("WARNING: Non-unique material definition! Was [" + materialType + "], gets [MIRROR]!")
        materialType = "MIRROR"
        material["color"] = [mat.diffuse_color[0], mat.diffuse_color[1], mat.diffuse_color[2]]

    if mat.emit > 0:
        if materialType is not "NULL":
            print("WARNING: Non-unique material definition! Was [" + materialType + "], gets [PLASTIC]!")
        materialType = "EMITTING"
        material["color"] = [mat.diffuse_color[0], mat.diffuse_color[1], mat.diffuse_color[2]]

    if materialType is "NULL":
        #fallback to DIFFUSE
        materialType = "DIFFUSE"
        material["color"] = [mat.diffuse_color[0], mat.diffuse_color[1], mat.diffuse_color[2]]

    print("Identified " + mat.name + " as " + materialType+"\n")
    material["type"] = materialType
    materialExport.append(material)
    materialDict[mat.name] = matIdx
    matIdx += 1

matfile.write(json.dumps(materialExport))

# --------------------- Object Geometry export -----------------------------

if writePackedBinary is False:
    meshfile.write("[")  # manual json wrapper to save memory while exporting very large scenes

exportedMeshes = 0
polyCount = 0
for obj in objList:

    if obj.type == "CAMERA":
        cam = obj.data
        if cam.type != "PERSP":
            print('no support for camera models other than \'perspective\'. Ignoring ' + cam.name)
            continue
        else:
            print("Exporting PERSP Camera")
            focalLength = (cam.lens/cam.sensor_width)*36.0
            objmatrix = obj.matrix_world

            eyeV = mathutils.Vector([0, 0, 0, 1])
            targetV = mathutils.Vector([0, 0, 1, 0])
            upV = mathutils.Vector([0, 1, 0, 0])

            eyeV = eyeV * objmatrix
            dirV = targetV * objmatrix
            upV = upV * objmatrix

            camExport = {}
            camExport["position"] = [obj.location[0], obj.location[1], obj.location[2]]
            camExport["rotation"] = [obj.rotation_euler[0], obj.rotation_euler[1], obj.rotation_euler[2]]
            camExport["viewDirection"] = [dirV[0], dirV[1], dirV[2]]
            camExport["upVector"] = [upV[0], upV[1], upV[2]]

            camExport["focalLength"] = focalLength
            camfile.write(json.dumps(camExport))

    if obj.type == "MESH":
        print('Exporting a mesh object: ' + obj.name + '(' + obj.data.name + ')')
        objMesh = obj.to_mesh(scene, True, 'RENDER')
        objMesh.transform(obj.matrix_world, True)
        if writePackedBinary:
            for face in objMesh.polygons:
                p0 = objMesh.vertices[face.vertices[0]].co
                p1 = objMesh.vertices[face.vertices[1]].co
                p2 = objMesh.vertices[face.vertices[2]].co

                meshfile.write(struct.pack("fff", p0.x, p0.y, p0.z))
                meshfile.write(struct.pack("fff", p1.x, p1.y, p1.z))
                meshfile.write(struct.pack("fff", p2.x, p2.y, p2.z))
                meshfile.write(struct.pack("B", materialDict[objMesh.materials[face.material_index].name]))
                polyCount += 1
        else:
            if exportedMeshes > 0:
                meshfile.write(", ")
            mesh = {}
            mesh["name"] = obj.name
            mesh["type"] = "TRIANGULAR_MESH"
            mesh["triangles"] = []

            for face in objMesh.polygons:
                p0 = objMesh.vertices[face.vertices[0]].co
                p1 = objMesh.vertices[face.vertices[1]].co
                p2 = objMesh.vertices[face.vertices[2]].co
                mesh["triangles"].append({"p0": [p0.x, p0.y, p0.z], "p1": [p1.x, p1.y, p1.z], "p2": [p2.x, p2.y, p2.z],
                                          "m": materialDict[objMesh.materials[face.material_index].name]})
                polyCount += 1

            meshfile.write(json.dumps(mesh))
            exportedMeshes += 1

if exportedMeshes > 0 and writePackedBinary is False:
    meshfile.write("]\n")

meshfile.close()
matfile.close()
camfile.close()

print("---------Statistics---------")
print("Nr. of Materials:  " + str(matIdx))
print("Nr. of Meshes:     " + str(exportedMeshes))
print("Nr. of Polygons:   " + str(polyCount))
print("Nr. of Cameras:    1")
print("----------------------------")
print("Have fun!")


