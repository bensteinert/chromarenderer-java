package net.chroma.math;

/**
 * @author steinerb
 */
public class ImmutableMatrix3x3 {

    ImmutableVector3[] columns;

    public ImmutableMatrix3x3(Vector3 col1, Vector3 col2, Vector3 col3) {
        this.columns = new ImmutableVector3[3];
        columns[0] = new ImmutableVector3(col1);
        columns[1] = new ImmutableVector3(col2);
        columns[2] = new ImmutableVector3(col3);
    }


//    public ImmutableMatrix3x3 invert() {
//
//        float temp[9];
//        float invDet = 1.f/(col1[0]*col2[1]*col3[2] - col1[0]*col3[1]*col2[2] - col2[0]*col1[1]*col3[2]
//                + col2[0]*col3[1]*col1[2] + col3[0]*col1[1]*col2[2] - col3[0]*col2[1]*col1[2]);
//
//        temp[0] = col2[1]*col3[2] - col3[1]*col2[2];
//        temp[1] = col3[0]*col2[2] - col2[0]*col3[2];
//        temp[2] = col3[0]*col3[1] - col3[0]*col2[1];
//        temp[3] = col3[1]*col1[2] - col3[2]*col1[1];
//        temp[4] = col1[0]*col3[2] - col3[0]*col1[2];
//        temp[5] = col2[0]*col1[2] - col1[0]*col2[2];
//        temp[6] = col1[1]*col2[2] - col1[2]*col2[1];
//        temp[7] = col3[0]*col1[1] - col1[0]*col3[1];
//        temp[8] = col1[0]*col2[1] - col2[0]*col1[1];
//
//        for(int i = 0; i < 3; i++){
//            this->col1[i] = invDet * temp[i*3];
//            this->col2[i] = invDet * temp[i*3+1];
//            this->col3[i] = invDet * temp[i*3+2];
//        }
//    }
//
//    public ImmutableMatrix3x3 orthogonalize()
//    {
//        Vector3 tempA2, tempA3;
//        col1 = col1*(1.0f/col1.length());
//        tempA2 = col2 - (col1*col2)*col1;
//        col2 = tempA2*(1.0f/tempA2.length());
//        tempA3 = col3 - (col1*col3)*col1 - (col2*col3)*col2;
//        col3 = tempA3*(1.0f/tempA3.length());
//    }
//
//    public ImmutableMatrix3x3 transpose()
//    {
//        Matrix3x3 m;
//
//        m.col1[0] = col1[0];
//        m.col1[1] = col2[0];
//        m.col1[2] = col3[0];
//
//        m.col2[0] = col1[1];
//        m.col2[1] = col2[1];
//        m.col2[2] = col3[1];
//
//        m.col3[0] = col1[2];
//        m.col3[1] = col2[2];
//        m.col3[2] = col3[2];
//
//        return m;
//    }
    public ImmutableMatrix3x3 mult(ImmutableMatrix3x3 input) {

        ImmutableVector3 row1 = row1();
        ImmutableVector3 row2 = row2();
        ImmutableVector3 row3 = row3();

        return new ImmutableMatrix3x3(new ImmutableVector3(row1.dot(input.col1()),
                                                           row2.dot(input.col1()),
                                                           row3.dot(input.col1())),
                                                             new ImmutableVector3(row1.dot(input.col2()),
                                                                                  row2.dot(input.col2()),
                                                                                  row3.dot(input.col2())),
                                                                                    new ImmutableVector3(row1.dot(input.col3()),
                                                                                                         row2.dot(input.col3()),
                                                                                                         row3.dot(input.col3())));
    }

    private ImmutableVector3 row1() {
        return new ImmutableVector3(columns[0].getX(), columns[1].getX(), columns[2].getX());
    }

    private ImmutableVector3 row2() {
        return new ImmutableVector3(columns[0].getY(), columns[1].getY(), columns[2].getY());
    }

    private ImmutableVector3 row3() {
        return new ImmutableVector3(columns[0].getZ(), columns[1].getZ(), columns[2].getZ());
    }

    private ImmutableVector3 col1() {
        return columns[0];
    }

    private ImmutableVector3 col2() {
        return columns[1];
    }

    private ImmutableVector3 col3() {
        return columns[2];
    }

}
