//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - December 31 2007 - Oscar Chavarro: Original base version              =
//===========================================================================

package vsdk.toolkit.gui;

public class KeyEvent extends PresentationElement
{
    public int keycode;
    public int modifierMask;

    public static final int MASK_CTRL = 0x0001;
    public static final int MASK_LCTRL = 0x0002;
    public static final int MASK_RCTRL = 0x0004;
    public static final int MASK_ALT = 0x0008;
    public static final int MASK_LALT = 0x0010;
    public static final int MASK_RALT = 0x0020;
    public static final int MASK_ALTGR = 0x0040;
    public static final int MASK_SHIFT = 0x0080;
    public static final int MASK_LSHIFT = 0x0100;
    public static final int MASK_RSHIFT = 0x0200;
    public static final int MASK_WINKEY = 0x0400;

    public static final int KEY_NONE = 0x0000;
    public static final int KEY_A = 0x0001;
    public static final int KEY_B = 0x0002;
    public static final int KEY_C = 0x0003;
    public static final int KEY_D = 0x0004;
    public static final int KEY_E = 0x0005;
    public static final int KEY_F = 0x0006;
    public static final int KEY_G = 0x0007;
    public static final int KEY_H = 0x0008;
    public static final int KEY_I = 0x0009;
    public static final int KEY_J = 0x000A;
    public static final int KEY_K = 0x000B;
    public static final int KEY_L = 0x000C;
    public static final int KEY_M = 0x000D;
    public static final int KEY_N = 0x000E;
    public static final int KEY_O = 0x000F;
    public static final int KEY_P = 0x0010;
    public static final int KEY_Q = 0x0011;
    public static final int KEY_R = 0x0012;
    public static final int KEY_S = 0x0013;
    public static final int KEY_T = 0x0014;
    public static final int KEY_U = 0x0015;
    public static final int KEY_V = 0x0016;
    public static final int KEY_W = 0x0017;
    public static final int KEY_X = 0x0018;
    public static final int KEY_Y = 0x0019;
    public static final int KEY_Z = 0x001A;
    public static final int KEY_a = 0x001B;
    public static final int KEY_b = 0x001C;
    public static final int KEY_c = 0x001D;
    public static final int KEY_d = 0x001E;
    public static final int KEY_e = 0x001F;
    public static final int KEY_f = 0x0020;
    public static final int KEY_g = 0x0021;
    public static final int KEY_h = 0x0022;
    public static final int KEY_i = 0x0023;
    public static final int KEY_j = 0x0024;
    public static final int KEY_k = 0x0025;
    public static final int KEY_l = 0x0026;
    public static final int KEY_m = 0x0027;
    public static final int KEY_n = 0x0028;
    public static final int KEY_o = 0x0029;
    public static final int KEY_p = 0x002A;
    public static final int KEY_q = 0x002B;
    public static final int KEY_r = 0x002C;
    public static final int KEY_s = 0x002D;
    public static final int KEY_t = 0x002E;
    public static final int KEY_u = 0x002F;
    public static final int KEY_v = 0x0030;
    public static final int KEY_w = 0x0031;
    public static final int KEY_x = 0x0032;
    public static final int KEY_y = 0x0033;
    public static final int KEY_z = 0x0034;
    public static final int KEY_0 = 0x0035;
    public static final int KEY_1 = 0x0036;
    public static final int KEY_2 = 0x0037;
    public static final int KEY_3 = 0x0038;
    public static final int KEY_4 = 0x0039;
    public static final int KEY_5 = 0x003A;
    public static final int KEY_6 = 0x003B;
    public static final int KEY_7 = 0x003C;
    public static final int KEY_8 = 0x003D;
    public static final int KEY_9 = 0x003E;
    public static final int KEY_NUM0 = 0x003F;
    public static final int KEY_NUM1 = 0x0040;
    public static final int KEY_NUM2 = 0x0041;
    public static final int KEY_NUM3 = 0x0042;
    public static final int KEY_NUM4 = 0x0043;
    public static final int KEY_NUM5 = 0x0044;
    public static final int KEY_NUM6 = 0x0045;
    public static final int KEY_NUM7 = 0x0046;
    public static final int KEY_NUM8 = 0x0047;
    public static final int KEY_NUM9 = 0x0048;
    public static final int KEY_F1 = 0x0049;
    public static final int KEY_F2 = 0x004A;
    public static final int KEY_F3 = 0x004B;
    public static final int KEY_F4 = 0x004C;
    public static final int KEY_F5 = 0x004D;
    public static final int KEY_F6 = 0x004E;
    public static final int KEY_F7 = 0x004F;
    public static final int KEY_F8 = 0x0050;
    public static final int KEY_F9 = 0x0051;
    public static final int KEY_F10 = 0x0052;
    public static final int KEY_F11 = 0x0053;
    public static final int KEY_F12 = 0x0054;
    public static final int KEY_ESC = 0x0055;
    public static final int KEY_PRINTSCREEN = 0x0056;
    public static final int KEY_BACKSPACE = 0x0057;
    public static final int KEY_INSERT = 0x0058;
    public static final int KEY_DELETE = 0x0059;
    public static final int KEY_PAGEUP = 0x005A;
    public static final int KEY_PAGEDOWN = 0x005B;
    public static final int KEY_HOME = 0x005C;
    public static final int KEY_END = 0x005D;
    public static final int KEY_SPACE = 0x005E;
    public static final int KEY_LSHIFT = 0x005F;
    public static final int KEY_RSHIFT = 0x0060;
    public static final int KEY_LALT = 0x0061;
    public static final int KEY_RALT = 0x0062;
    public static final int KEY_ALTGR = 0x0063;
    public static final int KEY_LCTRL = 0x0064;
    public static final int KEY_RCTRL = 0x0065;
    public static final int KEY_UP = 0x0066;
    public static final int KEY_DOWN = 0x0067;
    public static final int KEY_LEFT = 0x0068;
    public static final int KEY_RIGHT = 0x0069;
    public static final int KEY_NUMSLASH = 0x006A;
    public static final int KEY_NUMASTERISK = 0x006B;
    public static final int KEY_NUMMINUS = 0x006C;
    public static final int KEY_NUMPLUS = 0x006D;
    public static final int KEY_NUMLOCK = 0x006E;
    public static final int KEY_NUMENTER = 0x006F;
    public static final int KEY_ENTER = 0x0070;
    public static final int KEY_CAPSLOCK = 0x0071;
    public static final int KEY_TAB = 0x0072;
    public static final int KEY_COMMA = 0x0073;
    public static final int KEY_PERIOD = 0x0074;
    public static final int KEY_NUMPERIOD = 0x0075;

    public  KeyEvent()
    {
        keycode = KEY_NONE;
        modifierMask = 0;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
