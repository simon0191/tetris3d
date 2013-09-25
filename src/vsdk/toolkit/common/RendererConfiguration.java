//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - October 11 2005 - Gabriel Sarmiento & Lina Rojas: Original base       =
//=                     version                                             =
//= - November 1 2005 - Oscar Chavarro: Quality check - comments added      =
//= - August 6 2006 - Oscar Chavarro: toString method added                 =
//===========================================================================

package vsdk.toolkit.common;

/**
The RendererConfiguration class is used to indicate some attributes in which a 
geometry is to be displayed in screen. The RendererConfiguration class is not
responsible of display any data, it is just an a suggestion on how to draw it.

@todo: Rename to 'RenderConfiguration' or something similar...
*/
public class RendererConfiguration extends FundamentalEntity /*implements Comparable <RendererConfiguration>*/ {

    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    public static final int SHADING_TYPE_NOLIGHT  = 0;
    public static final int SHADING_TYPE_FLAT     = 1;
    public static final int SHADING_TYPE_GOURAUD  = 2;
    public static final int SHADING_TYPE_PHONG    = 3;

    // Indicates the type of shading algorithm to apply in shaders. This must
    // be one of the values defined in the constants SHADING_TYPE_* of this
    // class.
    private int shadingType;

    private boolean surfaces;
    private boolean wires;
    private boolean boundingVolume;
    private boolean selectionCorners;
    private boolean texture;
    private boolean bumpMap;
    private boolean points;
    private boolean normals;
    private boolean trianglesNormals;
    private ColorRgb wireColor;

    public int compareTo(RendererConfiguration other)
    {
        long valThis;
        long valOther;

        valThis = 0x00;
        if ( this.surfaces ) valThis += 0x0001;
        if ( this.wires ) valThis += 0x0002;
        if ( this.boundingVolume ) valThis += 0x0004;
        if ( this.selectionCorners ) valThis += 0x0008;
        if ( this.texture ) valThis += 0x0010;
        if ( this.bumpMap ) valThis += 0x0020;
        if ( this.points ) valThis += 0x0040;
        if ( this.normals ) valThis += 0x0080;
        if ( this.trianglesNormals ) valThis += 0x0100;
        valThis += this.shadingType * 0x1000;

        valOther = 0x00;
        if ( other.surfaces ) valOther += 0x0001;
        if ( other.wires ) valOther += 0x0002;
        if ( other.boundingVolume ) valOther += 0x0004;
        if ( other.selectionCorners ) valOther += 0x0008;
        if ( other.texture ) valOther += 0x0010;
        if ( other.bumpMap ) valOther += 0x0020;
        if ( other.points ) valOther += 0x0040;
        if ( other.normals ) valOther += 0x0080;
        if ( other.trianglesNormals ) valOther += 0x0100;
        valOther += other.shadingType * 0x1000;

        if ( valThis > valOther ) {
            return 1;
	}
	else if ( valThis < valOther ) {
            return -1;
	}
	return 0;
    }

    public void clone(RendererConfiguration other)
    {
        this.shadingType = other.shadingType;
        this.surfaces = other.surfaces;
        this.wires = other.wires;
        this.boundingVolume = other.boundingVolume;
        this.selectionCorners = other.selectionCorners;
        this.texture = other.texture;
        this.bumpMap = other.bumpMap;
        this.points = other.points;
        this.normals = other.normals;
        this.trianglesNormals = other.trianglesNormals;
        this.wireColor.clone(other.wireColor);
    }

    // To be used in future: depending of the rendering implementation for
    // each geometry, this can be used to specify the desired amount of
    // primitives to generate in dynamic or progressive geometry rendering
    // algorithms.
    private int lodHint;

    public void setLodHint(int l)
    {
        lodHint = l;
    }

    public int getLodHint()
    {
        return lodHint;
    }

    /**
    Constructs a default RendererConfiguration object that will display the surfaces
    */
    public RendererConfiguration() {
        shadingType = SHADING_TYPE_GOURAUD;

        surfaces = true;
        wires = false;
        boundingVolume = false;
        selectionCorners = false;
        texture = true;
        bumpMap = false;
        points = false;
        normals = false;
        trianglesNormals = false;
        wireColor = new ColorRgb(1, 1, 1);
    }

    public void setWireColor(ColorRgb c)
    {
        wireColor.clone(c);
    }

    public void setWireColor(double r, double g, double b)
    {
        wireColor.r = r;
        wireColor.g = g;
        wireColor.b = b;
    }

    public ColorRgb getWireColor()
    {
        return wireColor;
    }

    /**
    Selects wether or not to display the surface of the object
    @param b The selection value, true indicates that the quality should be 
    displayed and false indicates that it shouldn't
    */
    public void setSurfaces(boolean b)
    {
        this.surfaces = b;
    }

    public void setWires(boolean b)
    {
        this.wires = b;
    }

    public void setBoundingVolume(boolean b)
    {
        this.boundingVolume = b;
    }

    public void setSelectionCorners(boolean c)
    {
        this.selectionCorners = c;
    }

    public void setTexture(boolean b)
    {
        this.texture = b;
    }

    public void setBumpMap(boolean b)
    {
        this.bumpMap = b;
    }

    public void setPoints(boolean b)
    {
        this.points = b;
    }

    public void setNormals(boolean b)
    {
        this.normals = b;
    }

    public void setTrianglesNormals(boolean b)
    {
        this.trianglesNormals = b;
    }

    public void setShadingType(int shadingType)
    {
        this.shadingType = shadingType;
    }

    public boolean isSurfacesSet()
    {
        return this.surfaces;
    }

    public boolean isWiresSet()
    {
        return this.wires;
    }

    public boolean isBoundingVolumeSet()
    {
        return this.boundingVolume;
    }

    public boolean isSelectionCornersSet()
    {
        return this.selectionCorners;
    }

    public boolean isTextureSet()
    {
        return this.texture;
    }

    public boolean isBumpMapSet()
    {
        return this.bumpMap;
    }

    public boolean isPointsSet()
    {
        return this.points;
    }

    public boolean isNormalsSet()
    {
        return this.normals;
    }

    public boolean isTrianglesNormalsSet()
    {
        return this.trianglesNormals;
    }

    public int getShadingType()
    {
        return this.shadingType;
    }

    public void changeSurfaces()
    {
        surfaces = !surfaces;
    }

    public void changeWires()
    {
        wires = !wires;
    }

    public void changeBoundingVolume()
    {
        boundingVolume = !boundingVolume;
    }

    public void changeSelectionCorners()
    {
        selectionCorners = !selectionCorners;
    }

    public void changeTexture()
    {
        texture = !texture;
    }

    public void changeBumpMap()
    {
        bumpMap = !bumpMap;
    }

    public void changePoints()
    {
        points = !points;
    }

    public void changeNormals()
    {
        normals = !normals;
    }

    public void changeTrianglesNormals()
    {
        this.trianglesNormals = !this.trianglesNormals;
    }

    public void changeShadingType()
    {
        shadingType++;
        if ( shadingType == 4 ) {
            shadingType = 0;
        }
    }

    public String toString()
    {
        String msg;

        msg = "<RendererConfiguration>:\n";

        msg = msg + "  - Shading type: ";
        switch ( shadingType ) {
          case SHADING_TYPE_NOLIGHT:
            msg = msg + "LIGHTING DISABLED (ONLY AMBIENT COLOR)\n";
            break;
          case SHADING_TYPE_FLAT:
            msg = msg + "FLAT\n";
            break;
          case SHADING_TYPE_GOURAUD:
            msg = msg + "GOURAUD\n";
            break;
          case SHADING_TYPE_PHONG:
            msg = msg + "PHONG\n";
            break;
          default:
            msg = msg + "INVALID!\n";
            break;
        }

        msg = msg + "  - Draw points: " + (points?"ON":"OFF") + "\n";
        msg = msg + "  - Draw wires: " + (wires?"ON":"OFF") + "\n";
        msg = msg + "  - Draw surfaces: " + (surfaces?"ON":"OFF") + "\n";
        msg = msg + "  - Draw bounding volume: " + (boundingVolume?"ON":"OFF") + "\n";
        msg = msg + "  - Draw selection corners: " + (selectionCorners?"ON":"OFF") + "\n";
        msg = msg + "  - Draw normals: " + (normals?"ON":"OFF") + "\n";
        msg = msg + "  - Draw triangles normals: " + (trianglesNormals?"ON":"OFF") + "\n";
        msg = msg + "  - With texture: " + (texture?"ON":"OFF") + "\n";
        msg = msg + "  - With bump map: " + (bumpMap?"ON":"OFF") + "\n";

        return msg;
    }

    public RendererConfiguration clone()
    {
        RendererConfiguration copy = new RendererConfiguration();
        copy.shadingType = shadingType;
        copy.surfaces = surfaces;
        copy.wires = wires;
        copy.boundingVolume = boundingVolume;
        copy.selectionCorners = selectionCorners;
        copy.texture = texture;
        copy.bumpMap = bumpMap;
        copy.points = points;
        copy.normals = normals;
        copy.trianglesNormals = trianglesNormals;
        copy.lodHint = lodHint;
        return copy;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
