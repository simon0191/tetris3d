//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - April 22 2007 - Oscar Chavarro: Original base version                 =
//===========================================================================

package vsdk.toolkit.common;

/**
DEFINITION: A `FundamentalEntity` in VitralSDK is a basic software construction
brick, use in the support and definition of common datastructures. Classes
derived from this abstract class belong two one of the following groups:
  - Extended common data structures: similar to other, but not found in basic
    java implementations. For example, Vector3D, Matrix4x4, ColorRgb and
    Quaternion classes, are 3D counterparts of 2D versions available in
    java language or java extensions like Java2D or Swing. These classes are
    provided as they replaces others with no 3D support, and as they are
    needed in all VitralSDK implementations. Note that they replace the use
    of not common/portable counterparts like Java3D. It is common to find
    these are NOT "classes", but "structs", and as such, they do not use
    encapsulation (i.e. they have public attributes), as they are not
    suppose to evolve nor change, and as they impact greatly in application
    performance.
  - Extended collections: similiar to other, but not found in basic
    java implementations. For example, CircularDoubleLinkedList implementation
    is similar to LinkedList included with java.util package.
  - Fundamental data element common in 3D computer graphics, like Ray's,
    Triangle's and Vertex'es.
  - Fundamental elements defined as unifying in VitralSDK platform, like
    RendererConfiguration.

The FundamentalEntity abstract class provides an interface for commonly used
data structuring classes. This serves two purposes:
  - To help in design level organization of commonly used classes (this
    eases the study of the class hierarchy)
  - To provide a place to locate possible future operations, common to all
    fundamental objects. Note that currently none of such operations have been
    detected.
*/
public abstract class FundamentalEntity extends Entity {
    // Yes, it is currently empty!
    ;
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
