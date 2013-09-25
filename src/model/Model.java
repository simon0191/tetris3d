package model;

import model.shapes.Shape;

public class Model {

    public static final int YES = 0;
    public static final int NO = 1;
    public static final int MAYBE = 2;
    public static final int MAX_HEIGHT = 9;
    public static boolean band = false;
    private int height = 0;
    private boolean[][][] matrix;

    public Model() {
        resetMatrix();
    }

    /*public boolean addShape(Shape shape) {
     if (shape.getLevel() == MAX_HEIGHT - 1) {
     // TODO: agregar shape a matriz
     return true;
     }
     else {
     boolean[][][] shapeMatrix = shape.getMatrix();
     for (int i = shapeMatrix.length - 1; i >= 0; --i) {
     for (int j = 0; j < shapeMatrix.length; ++j) {
     for (int k = 0; k < shapeMatrix.length; ++k) {
     if (shapeMatrix[i][j][k]
     && matrix[i + shape.getDesfX()][j
     + shape.getDesfY()][shape.getLevel() + 1]) {
     // TODO: agregar shape a matriz
     return true;
     }
     }
     }
     }
     }
     shape.setLevel(shape.getLevel() + 1);
     return false;
     }
     */
    public void resetMatrix() {
        matrix = new boolean[7][7][MAX_HEIGHT + 1];
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                for (int k = 0; k < matrix[i][j].length; ++k) {
                    if (k == MAX_HEIGHT || i == 0 || i == 6 || j == 0 || j == 6) {
                        matrix[i][j][k] = true;
                    }
                }
            }
        }
    }

    public boolean[][][] getMatrix() {
        return matrix;
    }

    public void setMatrix(boolean[][][] matrix) {
        this.matrix = matrix;
    }

    public void incrementDesfX(Shape shape) {
        boolean[][][] shapeMatrix = shape.getMatrix();
        boolean band = false;

        for (int k = 0; k < shapeMatrix[0][0].length; ++k) {
            int maxX = 0;
            for (int j = 0; j < shapeMatrix[0].length; ++j) {
                band = false;
                for (int i = 0; i < shapeMatrix.length; ++i) {
                    if (shapeMatrix[i][j][k]) {
                        band = true;
                        maxX = Math.max(maxX, i);
                    }
                }
                if (band
                        && matrix[maxX + 1 + shape.getDesfX()][j
                        + shape.getDesfY()][k + shape.getLevel()]) {
                    return;
                }
            }
        }
        shape.incrementDesfX();
    }

    public void decrementDesfX(Shape shape) {
        boolean[][][] shapeMatrix = shape.getMatrix();
        boolean band = false;
        ;
        for (int k = 0; k < shapeMatrix[0][0].length; ++k) {
            int minX = 0;
            for (int j = 0; j < shapeMatrix[0].length; ++j) {
                band = false;
                for (int i = 0; i < shapeMatrix.length; ++i) {
                    if (shapeMatrix[i][j][k]) {
                        band = true;
                        minX = Math.min(minX, i);
                    }
                }
                if (band
                        && matrix[minX - 1 + shape.getDesfX()][j
                        + shape.getDesfY()][k + shape.getLevel()]) {
                    return;
                }
            }
        }
        shape.decrementDesfX();
    }

    public void incrementDesfY(Shape shape) {
        boolean[][][] shapeMatrix = shape.getMatrix();
        boolean band = false;
        for (int k = 0; k < shapeMatrix[0][0].length; ++k) {
            int maxY = 0;
            for (int i = 0; i < shapeMatrix.length; ++i) {
                band = false;
                for (int j = 0; j < shapeMatrix[i].length; ++j) {
                    if (shapeMatrix[i][j][k]) {
                        band = true;
                        maxY = Math.max(maxY, j);
                    }
                }
                if (band
                        && matrix[i + shape.getDesfX()][maxY + shape.getDesfY()
                        + 1][k + shape.getLevel()]) {
                    return;
                }
            }
        }
        shape.incrementDesfY();
    }

    public void decrementDesfY(Shape shape) {
        boolean[][][] shapeMatrix = shape.getMatrix();
        boolean band = false;
        for (int k = 0; k < shapeMatrix[0][0].length; ++k) {
            int minY = 0;
            for (int i = 0; i < shapeMatrix.length; ++i) {
                band = false;
                for (int j = 0; j < shapeMatrix[i].length; ++j) {
                    if (shapeMatrix[i][j][k]) {
                        band = true;
                        minY = Math.min(minY, j);
                    }
                }
                if (band
                        && matrix[i + shape.getDesfX()][minY + shape.getDesfY()
                        - 1][k + shape.getLevel()]) {
                    return;
                }
            }
        }
        shape.decrementDesfY();
    }

    private boolean rotate(Shape shape, int rotation) {
        shape.rotate(rotation);
        int x, y, z;
        boolean[][][] shapeMatrix = shape.getMatrix();

        for (int i = 0; i < shapeMatrix.length; ++i) {
            for (int j = 0; j < shapeMatrix[i].length; ++j) {
                for (int k = 0; k < shapeMatrix[i][j].length; ++k) {
                    x = i + shape.getDesfX();
                    y = j + shape.getDesfY();
                    z = k + shape.getLevel();
                    if (x < matrix.length && y < matrix[0].length && z < matrix[0][0].length) {
                        if (matrix[x][y][z] && shapeMatrix[i][j][k]) {
                            for (int p = 0; p < 3; ++p) {
                                shape.rotate(rotation);
                            }
                            return false;
                        }
                    }

                }
            }
        }

        return true;
    }

    public boolean rotateYes(Shape shape) {
        return rotate(shape, YES);
    }

    private boolean rotateReverse(Shape shape, int rotation) {
        if (rotate(shape, rotation)) {
            if (rotate(shape, rotation)) {
                if (rotate(shape, rotation)) {
                    return true;
                } else {
                    shape.rotate(rotation);
                    shape.rotate(rotation);
                }
            } else {
                shape.rotate(rotation);
                shape.rotate(rotation);
                shape.rotate(rotation);
            }
        }
        return false;
    }

    public boolean rotateYesReverse(Shape shape) {
        return rotateReverse(shape, YES);
    }

    public boolean rotateNo(Shape shape) {
        return rotate(shape, NO);
    }

    public boolean rotateNoReverse(Shape shape) {
        return rotateReverse(shape, NO);
    }

    public boolean rotateMaybe(Shape shape) {
        return rotate(shape, MAYBE);
    }

    public boolean rotateMaybeReverse(Shape shape) {
        return rotateReverse(shape, MAYBE);
    }

    public boolean incrementLevel(Shape shape) {
        boolean[][][] shapeMatrix = shape.getMatrix();
        boolean band = false;
        boolean unir = false;
        int maxZ = 0;
        int x, y, z;
        for (int i = 0; i < shapeMatrix.length && !unir; ++i) {
            for (int j = 0; j < shapeMatrix[i].length && !unir; ++j) {
                band = false;
                maxZ = 0;
                for (int k = 0; k < shapeMatrix[0][0].length; ++k) {
                    if (shapeMatrix[i][j][k]) {
                        band = true;
                        maxZ = Math.max(maxZ, k);
                    }
                }
                x = i + shape.getDesfX();
                y = j + shape.getDesfY();
                z = maxZ + shape.getLevel() + 1;
                if (x < matrix.length && y < matrix[0].length && z < matrix[0][0].length) {
                    if (band && matrix[x][y][z]) {
                        unir = true;
                    }
                }
            }
        }
        if (unir) {
            for (int i = 0; i < shapeMatrix.length; ++i) {
                for (int j = 0; j < shapeMatrix[i].length; ++j) {
                    for (int k = 0; k < shapeMatrix[i][j].length; ++k) {
                        if (shapeMatrix[i][j][k]) {
                            matrix[i + shape.getDesfX()][j + shape.getDesfY()][k + shape.getLevel()] = true;
                        }
                    }
                }
            }
            verifyFloor();
            return true;
        }
        shape.setLevel(shape.getLevel() + 1);
        return false;
    }

    public synchronized boolean incrementLevel2(Shape shape) {
        boolean[][][] shapeMatrix = shape.getMatrix();
        int x, y, z;
        for (int k = 0; k < shapeMatrix[0][0].length; ++k) {
            for (int i = 0; i < shapeMatrix.length; ++i) {
                for (int j = 0; j < shapeMatrix[0].length; ++j) {
                    x = i + shape.getDesfX();
                    y = j + shape.getDesfY();
                    z = k + shape.getLevel() + 1;
                    if (x > 0 && x < matrix.length - 1 && y > 0 && y < matrix[0].length - 1 && z < matrix[0][0].length) {

                        if (matrix[x][y][z]) {
                            System.out.println("===========");
                            System.out.println("X: " + x);
                            System.out.println("Y: " + y);
                            System.out.println("Z: " + z);
                            System.out.println("d");
                            joinShape(shape);
                            return true;
                        }
                    }
                }
            }
        }
        shape.setLevel(shape.getLevel() + 1);
        return false;
    }

    private void joinShape(Shape shape) {
        boolean[][][] shapeMatrix = shape.getMatrix();
        for (int i = 0; i < shapeMatrix.length; ++i) {
            for (int j = 0; j < shapeMatrix[i].length; ++j) {
                for (int k = 0; k < shapeMatrix[i][j].length; ++k) {
                    if (shapeMatrix[i][j][k]) {
                        matrix[i + shape.getDesfX()][j + shape.getDesfY()][k + shape.getLevel()] = true;
                    }
                }
            }
        }
        verifyFloor();
    }

    private void verifyFloor() {
        boolean completeFloor;
        for (int k = 0; k < matrix[0][0].length - 1; ++k) {
            completeFloor = true;
            for (int i = 0; i < matrix.length && completeFloor; ++i) {
                for (int j = 0; j < matrix[i].length && completeFloor; ++j) {
                    if (!matrix[i][j][k]) {
                        completeFloor = false;
                    }
                }
            }
            if (completeFloor) {
                for (int kk = k; kk > 0; --kk) {
                    for (int i = 1; i < matrix.length - 1; ++i) {
                        for (int j = 1; j < matrix[i].length - 1; ++j) {
                            matrix[i][j][kk] = matrix[i][j][kk - 1];
                        }
                    }
                }
            }
        }
    }
}
