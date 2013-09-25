//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - May 18 2007 - Oscar Chavarro: Original base version                   =
//===========================================================================

package vsdk.toolkit.processing;

/**
DEFINITION: A `ProcessingElement` in VitralSDK is a software element with
algorithms and data structures (i.e. a class) with the specific functionality
for manipulating and extract metadata from a data Entity (usually a geometric
low-level entity). Usually, a `ProcessingElement` provides stand-alone,
self-contained 100% pure java algorithms, but can constitute also the
placeholder for wrappers that call external libraries and tools
(as VTK, ITK, image processing toolkits, computational geometry toolkits
and external utilities).

The ProcessingElement abstract class provides an interface for classes that
implement strategy like design patterns (classes that encapsulates algorithms).
This serves two purposes:
  - To help in design level organization of classes containing algorithms
    and associated working data (this eases the study of the class hierarchy)
  - To provide a place to locate possible future operations, common to
    all processing classes (but none of these as been detected yet)
*/

public abstract class ProcessingElement {
    ;
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
