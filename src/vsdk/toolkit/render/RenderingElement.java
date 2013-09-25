//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - December 8 2006 - Oscar Chavarro: Original base version               =
//===========================================================================

package vsdk.toolkit.render;

/**
DEFINITION: A `RenderingElement` in VitralSDK is a software element with
algorithms and data structures (i.e. a class) with the specific functionality
of generating simple visual 2D primitives from a data Entity (i.e. aiding
as part of a visualization pipeline). Usually, a `RenderingElement` provides
access to specific technologies as Awt or OpenGL/Jogl, and translates complex
Entity geometrical representations to simple primitives usable by such
technologies (i.e. lines and planar polygons to OpenGL/Jogl, and Java2D
and simple pixel data to Awt).

The RenderingElement abstract class provides an interface for *Renderer
style classes. This serves two purposes:
  - To help in design level organization of renderers (this eases the
    study of the class hierarchy)
  - To provide a place to locate possible future operations, common to
    all renderers classes and renderers' private utility/supporting
    classes (but none of these as been detected yet)
Note that this is a very high-level organizing class inside the Vitral SDK,
and it is supposed to be here to support operations common to any renderer,
independent of the rendering technology/interface. Possibly none operation
of such high level should be included here, but to this class subclasses
(the organizers of each specific rendering technology classes).
*/

public abstract class RenderingElement {
    ;
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
