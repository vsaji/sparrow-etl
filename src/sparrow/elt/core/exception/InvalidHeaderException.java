
package sparrow.elt.core.exception;

import java.io.IOException;

/**
 * This exception is used to indicate that there is a problem
 * with a TAR archive header.
 */

public class
InvalidHeaderException extends IOException
	{

	public
	InvalidHeaderException()
		{
		super();
		}

	public
	InvalidHeaderException( String msg )
		{
		super( msg );
		}

	}

