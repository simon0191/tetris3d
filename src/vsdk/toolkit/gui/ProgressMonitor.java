//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - March 19 2006 - Oscar Chavarro: Original base version                 =
//===========================================================================

package vsdk.toolkit.gui;

/**
This interface is designed to staandarize a way in which any slow or heavy
computation process provided by the VSDK toolkit can be monitored. Some
algorithms like raytracing use classes inherited from this interface to
inform to the main application or subsystem the relative advance in its
operations.  Note that this interface provides a mechanism to inform
advance to the containing applicacion in a technology independent way
(for example, this does not depend on specific GUI widgets). Note that
a simple reference implementation for console applications is provided in
this package.
*/
public abstract class ProgressMonitor extends PresentationElement {
    public abstract void begin();
    public abstract void end();
    public abstract void update(double minValue, double maxValue, double currentValue);
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
