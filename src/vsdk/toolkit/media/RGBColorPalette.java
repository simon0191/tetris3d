//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - February 24 2006 - Gina Chiquillo: Original base version              =
//= - April 4 2006 - Oscar Chavarro: Quality check - comments added         =
//= - May 26 2006 - David Diaz/Oscar Chavarro: documentation added          =
//===========================================================================

package vsdk.toolkit.media;

import java.util.ArrayList;

import vsdk.toolkit.common.ColorRgb;

/**
This class represents a color palette, as an indexed set of colors.
*/
public class RGBColorPalette extends MediaEntity {
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    protected ArrayList<ColorRgb> colors;

    /**
    Builds a default color pallete: the 256 tones gray scale
    */
    public RGBColorPalette()
    {
        colors = new ArrayList<ColorRgb>();
        init(256);
    }

    /**
    Initialize this color palette's data to a gray scale color palette with a 
    number of levels specified by the input parameter.
    @param size Initial size of gray scale for current color palette's 
    initialization
    */
    public void init(int size)
    {
        colors = new ArrayList<ColorRgb>();
        int i;

        ColorRgb c;
        for ( i = 0; i < size; i++ ) {
            c = new ColorRgb(0, 0, 0);
            colors.add(c);
        }
        buildGrayLevelsTable();
    }

    /**
    Returns the size of current palette
    @return The size of current palette
    */
    public int size()
    {
        return colors.size();
    }

    /**
    Given current RGBColorPalette assigned in memory, and withoun changing its
    size, this method change the palette's values to make it correspond to
    a gray scale.  The first values in the are near black, and the last values 
    in the table are near white.
    */
    public void buildGrayLevelsTable()
    {
        int pos = 0;
        double val = 0;
        int i;

        for ( i = 0; i < colors.size(); i++ ) {
            ColorRgb c = colors.get(i);
            c.r = c.g = c.b = val;
            val += 1.0/((double)(colors.size()-1));
            pos++;        
        }
    }

    /**
    Returns the exact color in the given index color table position. Note that
    this is a direct table manipulation method, as opposed to the `evalNearest`
    and `evalLinear` methods, which are the recomended ones to use for most
    applications.
    @param i The index of the color to return
    @return The color in the index parameter position
    */
    public ColorRgb getColorAt(int i)
    {
        if ( i < 0 || i >= colors.size() ) return null;
        return colors.get(i);
    }

    public void setColorAt(int i, ColorRgb c)
    {
        if ( i < 0 || i >= colors.size() ) return;
        colors.set(i, c);
    }

    public void setColorAt(int i, double r, double g, double b)
    {
        if ( i < 0 || i >= colors.size() ) return;

        colors.set(i, new ColorRgb(r, g, b));
    }

    public void addColor(ColorRgb c)
    {
        colors.add(c);
    }

    public void addColor(double r, double g, double b)
    {
        colors.add(new ColorRgb(r, g, b));
    }

    /**
    The parameter `t` must be between 0 and 1
    */
    public ColorRgb evalNearest(double t)
    {
        if ( t < 0.0 ) t = 0.0;
        if ( t > 1.0 ) t = 1.0;

        int N = colors.size();

        int i = (int)(t*((double)N));

        if ( i < 0 ) i = 0;
        if ( i >= N ) i = N-1;

        return colors.get(i);
    }

    /**
    The parameter `t` must be between 0 and 1.
    */
    public ColorRgb evalLinear(double t)
    {
        if ( t < 0.0 ) t = 0.0;
        if ( t > 1.0 ) t = 1.0;

        int N = colors.size()-1;

        int inf = (int)(t*((double)N));
        int sup = inf+1;

        double delta = 1/((double)N);
        double r = t - inf*delta;
        double p = r / delta;

        if ( inf < 0 ) inf = 0;
        if ( inf > N ) inf = N;
        if ( sup < 0 ) sup = 0;
        if ( sup > N ) sup = N;

        ColorRgb a = colors.get(inf);
        ColorRgb b = colors.get(sup);
        ColorRgb c = new ColorRgb(
            a.r + (b.r-a.r)*p,
            a.g + (b.g-a.g)*p,
            a.b + (b.b-a.b)*p    );

        return c;
    }

    public int selectNearestIndexToRgb(ColorRgb c)
    {
        double minDistance = Double.MAX_VALUE;
        int i;
        double currentDistance;
        int index = 0;

        for ( i = 0; i < colors.size(); i++ ) {
            currentDistance = colors.get(i).distance(c);
            if ( currentDistance < minDistance ) {
                minDistance = currentDistance;
                index = i;
            }
        }
        return index;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
