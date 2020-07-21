/**
 *
 */
package org.kawanfw.sql.api.server.util;

import java.util.Objects;

import org.kawanfw.sql.api.server.DefaultDatabaseConfigurator;

/**
 * Usernames must be converted by replacing Windows characters, because they are
 * used as directory names when uploading/downloading Blobs. <br>
 * Thus following characters are replaced for each username when reading/writing
 * on file system:
 *
 * <pre>
 * <code>
&lt; : __ac_lt__
&gt; : __ac_gt__
: : __ac_colon__
" : __ac_dbquote__
/ : __ac_fslash__
\ : __ac_bslash__
| : __ac_vbar__
? : __ac_qmark__
&lowast; : __ac_aster__
 </code>
 * </pre>
 *
 * Spaces are also replaced with {@code __ac_sp__}.
 * @see DefaultDatabaseConfigurator#getBlobsDirectory(String)
 * @author Nicolas de Pomereu
 * @since 5.0.2
 *
 */
public class UsernameConverter {

    private static final String AC_ASTER = "__ac_aster__";
    private static final String AC_QMARK = "__ac_qmark__";
    private static final String AC_VBAR = "__ac_vbar__";
    private static final String AC_BSLASH = "__ac_bslash__";
    private static final String AC_FSLASH = "__ac_fslash__";
    private static final String AC_DBQUOTE = "__ac_dbquote__";
    private static final String AC_COLON = "__ac_colon__";
    private static final String AC_GT = "__ac_gt__";
    private static final String AC_LT = "__ac_lt__";
    private static final String AC_SP = "__ac_sp__";

    /**
     * Protected class
     */
    protected UsernameConverter() {
	// TODO Auto-generated constructor stub
    }

    /**
     * Replace back space and specials characters forbidden in Windows file name
     * from ASCII string.
     *
     * @param string the string with replaced characters
     * @return the string with original spaces and Windows characters
     */
    public static String toSpecialChars(final String stringParm) {
	String string = stringParm.replace(AC_SP, " ");
	string = string.replace(AC_LT, "<");
	string = string.replace(AC_GT, ">");
	string = string.replace(AC_COLON, ":");
	string = string.replace(AC_DBQUOTE, "\"");
	string = string.replace(AC_FSLASH, "/");
	string = string.replace(AC_BSLASH, "\\");
	string = string.replace(AC_VBAR, "|");
	string = string.replace(AC_QMARK, "?");
	string = string.replace(AC_ASTER, "*");
	return string;
    }

    /**
     * Replace Windows special character and spaces by clear ASCII text
     *
     * @param string the string to replace from the Windows special characters
     * @return the string without special Windows characters
     */
    public static String fromSpecialChars(final String stringParm) {
	if (stringParm == null) {
	    Objects.requireNonNull(stringParm, "stringParm cannot be null!");
	}

	String string = stringParm.replace(" ", AC_SP);
	string = string.replace("<", AC_LT);
	string = string.replace(">", AC_GT);
	string = string.replace(":", AC_COLON);
	string = string.replace("\"", AC_DBQUOTE);
	string = string.replace("/", AC_FSLASH);
	string = string.replace("\\", AC_BSLASH);
	string = string.replace("|", AC_VBAR);
	string = string.replace("?", AC_QMARK);
	string = string.replace("*", AC_ASTER);

	return string;

    }

}
