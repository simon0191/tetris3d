//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - December 8 2006 - Oscar Chavarro: Original base version               =
//===========================================================================

package vsdk.toolkit.render.awt;

import vsdk.toolkit.render.RenderingElement;

/**
The AwtRenderer abstract class provides an interface for Awt*Renderer
style classes. This serves two purposes:
  - To help in design level organization of Awt renderers (this eases the
    study of the class hierarchy)
  - To provide a place to locate possible future operations, common to
    all Awt renderers classes and Awt renderers' private utility/supporting
    classes (but none of these as been detected yet)
*/

public abstract class AwtRenderer extends RenderingElement {
    ;
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
