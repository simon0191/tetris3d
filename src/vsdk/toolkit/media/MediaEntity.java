//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - April 22 2007 - Oscar Chavarro: Original base version                 =
//===========================================================================

package vsdk.toolkit.media;

import vsdk.toolkit.common.Entity;

/**
DEFINITION: A `MediaEntity` in VitralSDK is a software element with data
structures associated with multimedia information, and some minor basic
algorithms for suporting them.

The MediaEntity abstract class provides an interface for multimedia related
classes NOT related with 3D geometry. This serves two purposes:
  - To help in design level organization of multimedia related classes (this
    eases the study of the class hierarchy)
  - To provide a place to locate possible future operations, common to all
    multimedia objects. Note that currently none of such operations have been
    detected.
*/
public abstract class MediaEntity extends Entity {
    // Yes, it is currently empty!
    ;
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
