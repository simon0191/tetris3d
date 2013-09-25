//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - May 2 2006 - Oscar Chavarro: Original base version                    =
//===========================================================================

package vsdk.toolkit.common;

import java.io.Serializable;

/**
This class is a base superclass for all classes in the VSDK model. Note that
this class supports two functionalities:
  - As implementing the Serializable interface, this permits to serialize 
    all of the 
*/

public class Entity implements Serializable
{
    /**
    This is a value used for the standard java serialization mechanism to
    keep track of software versions.  To avoid warning at compilation time
    and to ease to keep compatibility tracking of software structure changes
    in retrieving old saved data, it is suggested that all Entity's in VSDK
    define this value.  The proposed number to asign is the concatenation of
    8 digits YYYYMMDD, for year, month and day respectively.
    */
    public static final long serialVersionUID = 20060502L;

    /**
    Each Entity object in the VSDK model should be responsible for calculating
    the size in bytes that occupies in RAM, including its own attributes and
    the agregated objects (note that the associated objects only must count
    the size of the references).  If the class doen't overload this method,
    a 0 size is assumed.

    This is important for applications implementing memory chaching schema.
    */
    public int getSizeInBytes()
    {
        return 0;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
