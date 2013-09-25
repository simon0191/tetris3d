//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - April 30 2008 - Oscar Chavarro: Original base version                 =
//===========================================================================

package vsdk.toolkit.common;

public class StopWatch
{
    private long t0;
    private long t1;

    public StopWatch()
    {
        t0 = 0;
        t1 = 1;
    }

    public void start()
    {
        //t0 = System.currentTimeMillis();
        t0 = System.nanoTime();
    }

    public void stop()
    {
        //t1 = System.currentTimeMillis();
        t1 = System.nanoTime();
    }

    public double getElapsedRealTime()
    {
        double a, b;
        a = (double)t0;
        b = (double)t1;
        //return (b - a)/1000.0;
        return (b - a)/1000000000.0;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
