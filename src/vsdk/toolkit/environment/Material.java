//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - August 8 2005 - Oscar Chavarro: Original base version                 =
//= - November 24 2005 - Oscar Chavarro: Revision and integration with      =
//=   recursive raytracing ilumination model                                =
//= - March 18 2006 - Oscar Chavarro: minor checks                          =
//===========================================================================

package vsdk.toolkit.environment;

import vsdk.toolkit.common.Entity;
import vsdk.toolkit.common.ColorRgb;

public class Material extends Entity
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    private ColorRgb ambient; 
    private ColorRgb diffuse; 
    private ColorRgb specular; 
    private ColorRgb emission;
    private boolean doubleSided;
    private double reflectionCoefficient;
    private double refractionCoefficient; // Also known as "transmition"

    private String name="VSDK_default_material";
    
    private double opacity;
    private double phongExponent;
    
    /** Creates a new instance of MaterialGL */
    public Material() 
    {
        ambient=new ColorRgb(0.1, 0.1, 0.1);
        diffuse=new ColorRgb(0.9, 0.5, 0.5);
        specular=new ColorRgb(1, 1, 1);
        emission=new ColorRgb(0, 0, 0);
        doubleSided = true;
        reflectionCoefficient = 0;
        opacity = 1.0;
        phongExponent = 128;
    }
    
    public Material(Material m) 
    {
        name=m.name;
        ambient=new ColorRgb(m.getAmbient()); 
        diffuse=new ColorRgb(m.getDiffuse()); 
        specular=new ColorRgb(m.getSpecular()); 
        emission=new ColorRgb(m.getEmission()); 
        doubleSided=m.doubleSided;
        opacity=m.getOpacity();
        phongExponent=m.phongExponent;
        reflectionCoefficient=m.reflectionCoefficient;
        refractionCoefficient=m.refractionCoefficient;
    }
    
    public void setName(String n)
    {
        name=n;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setAmbient(ColorRgb a)
    {
        this.ambient=new ColorRgb(a);
    }
    
    public void setDiffuse(ColorRgb d)
    {
        this.diffuse=new ColorRgb(d);
    }
    
    public void setSpecular(ColorRgb s)
    {
        specular=new ColorRgb(s);
    }
    
    public void setEmission(ColorRgb e)
    {
        emission=new ColorRgb(e);
    }
    
    public void setPhongExponent(double p)
    {
        this.phongExponent=p;
    }

    public void setReflectionCoefficient(double kr)
    {
        this.reflectionCoefficient=kr;
    }

    public void setRefractionCoefficient(double kr)
    {
        this.refractionCoefficient=kr;
    }
    
    public void setOpacity(double a)
    {
        this.opacity=a;
    }
    
    public boolean isDoubleSided()
    {
        return doubleSided;
    }

    public void setDoubleSided(boolean doubleSided)
    {
        this.doubleSided = doubleSided;
    }

    public ColorRgb getAmbient()
    {
        return new ColorRgb(ambient);
    }

    public ColorRgb getDiffuse()
    {
        return new ColorRgb(diffuse);
    }

    public ColorRgb getSpecular()
    {
        return new ColorRgb(specular);
    }

    public ColorRgb getEmission()
    {
        return new ColorRgb(emission);
    }

    public double getPhongExponent()
    {
        return phongExponent;
    }

    public double getReflectionCoefficient()
    {
        return reflectionCoefficient;
    }

    public double getRefractionCoefficient()
    {
        return refractionCoefficient;
    }
    
    public double getOpacity()
    {
        return opacity;
    }

    /**
    Provides an object to text report convertion, optimized for human
    readability and debugging. Do not use for serialization or persistence
    purposes.
    */
    public String toString()
    {
        return "Material [" + name + "]:\n" +
               "  - Specular " + specular + "\n" +
               "  - Diffuse " + diffuse + "\n" +
               "  - Ambient " + ambient + "\n" +
               "  - Phong exponent: " + phongExponent + "\n" +
               (isDoubleSided()?"  - Double sided\n":"  - Single sided\n") +
               "\n";
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
