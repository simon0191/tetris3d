//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - December 8 2006 - Oscar Chavarro: Original base version               =
//===========================================================================

package vsdk.toolkit.gui;

/**
DEFINITION: A `PresentationElement` in VitralSDK is a software element with
algorithms and data structures (i.e. a class) with the specific functionality
of managing (create, delete, query and modify) a data Entity from human
user interaction. Usually, a `PresentationElement` provides specific
human-machine interaction techniques to applications.

The PresentationElement abstract class provides an interface for graphical
user managment classes (GUI). This serves two purposes:
  - To help in design level organization of GUI elements (this eases the
    study of the class hierarchy)
  - To provide a place to locate possible future operations, common to
    all GUI classes and private utility/supporting GUI classes 
    (but none of these as been detected yet)
Note that as Vitral SDK is a computer graphics oriented architecture,
in its model the rendering operations are modelled apart from presentation
features, so, RenderingElement's are modelled in a different class
hierarchy.
*/

public abstract class PresentationElement {
    ;
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
