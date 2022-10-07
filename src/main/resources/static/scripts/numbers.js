// ***********************************************************
// Javascript function library
// Name:	aptfree.js
// Created by:	Affordable Production Tools
// Web site:	http://www.apt.simplenet.com
// Last update:	December 2, 1998
//
// Placed in the public domain by Affordable Production Tools
//
// It is requested that if you use the functions in this
// library, the origination notices remain with the functions.
// ***********************************************************

// **********************************************************
// Placed in the public domain by Affordable Production Tools
// March 21, 1998
// Web site: http://www.apt.simplenet.com
//
// November 24, 1998 -- Error which allowed a null value
// to remain null fixed. Now forces value to 0.
//
// This function accepts a number to format and number
// specifying the number of decimal places to format to. May
// optionally use a separator other than '.' if specified.
//
// If no decimals are specified, the function defaults to
// two decimal places. If no number is passed, the function
// defaults to 0. Decimal separator defaults to '.' .
//
// If the number passed is too large to format as a decimal
// number (e.g.: 1.23e+25), or if the conversion process
// results in such a number, the original number is returned
// unchanged.
// **********************************************************
function FormatNumber (Number, Decimals, Separator)
{
	Number += ""          // Force argument to string.
	Decimals += ""        // Force argument to string.
	Separator += ""       // Force argument to string.
	
	if (Separator == "" || Separator.length > 1)
		Separator = "."

	if (Number.length == 0)
		Number = "0"

	var OriginalNumber = Number  // Save for number too large.
	var Sign = 1
	var Pad = ""
	var Count = 0

	// If no number passed, force number to 0.
	if (parseFloat (Number))
		Number = parseFloat (Number)
	else
		Number = 0
	
	// If no decimals passed, default decimals to 2.
	if (parseInt (Decimals,10) || parseInt (Decimals,10) == 0)
		Decimals = parseInt (Decimals,10)
	else
		Decimals = 2
		
	if (Number < 0)	{
		Sign = -1;			// Remember sign of Number.
		Number *= Sign;		// Force absolute value of Number.
	}
	
	if (Decimals < 0)
		Decimals *= -1;		// Force absolute value of Decimals.
	
	// Next, convert number to rounded integer and force to string value.
	// (Number contains 1 extra digit used to force rounding)
	Number = "" + Math.floor (Number * Math.pow (10,Decimals + 1) + 5)
	
	if (Number.substring (1,2) == '.' || (Number + '') =='NaN')
		return OriginalNumber;		// Number too large to format as specified.

	// If length of Number is less than number of decimals requested +1,
	// pad with zeros to requested length.
	if (Number.length < Decimals +1) // Construct pad string.
	{
		for (Count = Number.length; Count <= Decimals; Count++)
			Pad += "0";
	}
	
	Number = Pad + Number // Pad number as needed.
	
	if (Decimals == 0) {
		// Drop extra digit -- Number is formatted.
		Number = Number.substring (0, Number.length -1)
	} else {
		// Or, format number with decimal point and drop extra decimal digit.
		Number = Number.substring (0,Number.length - Decimals -1) +
		Separator +
		Number.substring (Number.length - Decimals -1,
		Number.length -1)
	}
	
	if (Sign == -1)
		Number = "-" + Number  // Set sign of number.
	
	if (Number.length == 0)
		Number="0";

	return Number;
}

// Web site: http://www.apt.simplenet.com
//
// December 2, 1998 -- Modified to allow specification of
// pad character.
//
// This function accepts a number or string, and a number
// specifying the desired length. If the length is greater
// than the length of the value passed, the value is padded
// with spaces (default) or the specified pad character
// to the length specified.
//
// The function is useful in right justifying numbers or
// strings in HTML form fields.
// **********************************************************
function PadLeft (String, Length, PadChar)
{
	String += "";		// Force argument to string.
	Length += "";		// Force argument to string</b>.
	PadChar += "";		// Force argument to string.

	if (PadChar == "" || PadChar.length != 1)
		PadChar = " ";
	
	var Count = 0;
	var PadLength = 0;
	Length = parseInt (0 + Length, 10);
	
	if (Length <= String.length) // No padding necessary.
		return String;
	
	PadLength = Length - String.length;
	for (Count = 0; Count < PadLength; Count++)
		String = PadChar + String;
		
	return String;
}

// **********************************************************
// Placed in the public domain by Affordable Production Tools
// April 1, 1998
// Web site: http://www.apt.simplenet.com
//
// December 2, 1998 -- Modified to allow specification of
// pad character.
//
// This function accepts a number or string, and a number
// specifying the desired length. If the length is greater
// than the length of the value passed, the value is padded
// with spaces (default) or the specified pad character
// to the length specified.
// **********************************************************
function PadRight (String, Length, PadChar)
{
	String += "";		// Force argument to string.
	Length += "";		// Force argument to string.
	PadChar += "";		// Force argument to string.

	if (PadChar == "" || PadChar.length != 1)
		PadChar = " ";
	
	var Count = 0;
	var PadLength = 0;
	
	Length = parseInt (0 + Length, 10);
	
	if (Length <= String.length) // No padding necessary.
		return String;
	
	PadLength = Length - String.length;
	
	for (Count = 0; Count < PadLength; Count++)
		String += PadChar;

	return String;
}

// **********************************************************
// Placed in the public domain by Affordable Production Tools
// April 1, 1998
// Web site: http://www.apt.simplenet.com
//
// December 2, 1998 -- Modified to allow specification of
// pad character.
//
// This function accepts a number or string, and a number
// specifying the desired length. If the length is greater
// than the length of the value passed, the value is padded
// with spaces (default) or the specified pad character
// to the length specified.
//
// Uses functions PadLeft() and PadRight()
// **********************************************************
function PadCenter (String, Length, PadChar)
{
	String += ""		// Force argument to string.
	Length += ""		// Force argument to string.
	PadChar += ""		// Force argument to string.

	if (PadChar == "" || PadChar.length != 1)
		PadChar = " "
	
	var Count = 0
	var PadLength = 0
	var LeftPad = 0
	var RightPad = 0
	
	Length = parseInt (0 + Length, 10)
	
	if (Length <= String.length) // No padding necessary.
		return(String)
	
	PadLength = Length - String.length
	LeftPad = Math.floor (PadLength / 2)
	RightPad = PadLength - LeftPad
	String = PadLeft (String, LeftPad + String.length, PadChar)
	String = PadRight (String, RightPad + String.length, PadChar)
	
	return String;
}

// **********************************************************
// Placed in the public domain by Affordable Production Tools
// April 27, 1998
// Web site: http://www.apt.simplenet.com
//
// December 2, 1998 -- Modified to allow specification of
// character to be trimmed.
//
// This function trims spaces (default) or the specified
// character from the left of a string or form field.
// **********************************************************
function LeftTrim (String, TrimChar)
{
	String += "";		// Force argument to string.
	TrimChar += "";		// Force argument to string.
	
	if (TrimChar == "" || TrimChar.length != 1)
		TrimChar = " ";
	
	if (String.length == 0)
		return String;
	
	var Count = 0;
	for (Count = 0; Count < String.length; Count++)
	{
		if (String.charAt (Count) != TrimChar)
			return String.substring (Count, String.length);
	}

	return ""
}

// **********************************************************
// Placed in the public domain by Affordable Production Tools
// April 27, 1998
// Web site: http://www.apt.simplenet.com
//
// December 2, 1998 -- Modified to allow specification of
// character to be trimmed.
//
// This function trims spaces (default) or the specified
// character from the right of a string or form field.
// **********************************************************
function RightTrim (String,TrimChar)
{
	String += "";		// Force argument to string.
	TrimChar += "";		// Force argument to string.
	
	if (TrimChar == "" || TrimChar.length != 1)
		TrimChar = " ";
	
	if (String.length == 0)
		return String;
	
	var Count = 0
	for (Count = String.length -1; Count >= 0; Count--)
	{
		if (String.charAt(Count) != TrimChar)
			return String.substring (0, Count + 1);
	}
	
	return "";
}

// **********************************************************
// Placed in the public domain by Affordable Production Tools
// April 27, 1998
// Web site: http://www.apt.simplenet.com
//
// December 2, 1998 -- Modified to allow specification of
// character to be trimmed.
//
// This function trims spaces (default) or the specified
// character from the left and the right of a string or form field.
//
// Note that this functions uses two other library functions,
// TrimLeft() and TrimRight().
// **********************************************************
function AllTrim (String, TrimChar)
{
	String += "";		// Force argument to string.
	TrimChar += "";		// Force argument to string.

	if (TrimChar == "" || TrimChar.length != 1)
		TrimChar = " ";

	return RightTrim (LeftTrim (String, TrimChar), TrimChar);
}

// **********************************************************
// Placed in the public domain by Affordable Production Tools
// March 23, 1998
// Web site: http://www.apt.simplenet.com
//
// November 24, 1998 -- Error which allowed a null field
// to remain null fixed. Now forces value to 0.
//
// December 2, 1998 -- Modified to allow specification of
// pad character.
//
// This function formats a number in an HTML form field,
// setting the decimal precision and right justifying the
// number in the field. An optional decimal separator other
// than '.' may be specified and an optional pad character
// may be specified (default is space).
//
// Note that this function uses two other library functions,
// FormatNumber() and PadLeft().
//
// Usage: Call the function with an onblur or onchange event
// attached to the field:
//
// onblur="FormatNumberField(this,Decimals,Pad,[Separator],[PadChar])"
// where Decimals is the number of decimals desired and Pad
// is the size of the field.
// **********************************************************
function FormatNumberField (Object, Decimals, Pad, Separator, PadChar)
{
	if (Object.value == "")
		Object.value = "0";
	
	if (Object == null)
		return null;

	Separator += "";	// Force argument to string.

	if (Separator == "" || Separator.length > 1)
		Separator = ".";
	
	PadChar += "";
	
	if (PadChar == "" || PadChar.length != 1)
		PadChar = " ";
	
	Object.value = FormatNumber (Object.value, Decimals, Separator);
	//Object.value = PadLeft (Object.value, Pad, PadChar);
	
	return Object.value;
}


