/**
 * Copyright (C) 2015  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package mujava.util;

import java.lang.System;
import java.io.*;

/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public final class Debug {

    final static int DETAILED_LEVEL = 2;
    final static int SIMPLE_LEVEL = 1;
    final static int EMPTY_LEVEL = 0;

    private static int debugLevel = 0;

    public static void setDebugLevel( int level ) {
	debugLevel = level;
    }

    /**
     * for debug
     */
    protected static PrintStream out = System.out;

    /** Flush the stream. */
    public static void flush() {
        if (debugLevel > EMPTY_LEVEL)  out.flush();
    }

    /** Close the stream. */
    public static void close() {
        if (debugLevel > EMPTY_LEVEL)  out.close();
    }

    /**
     * Flush the stream and check its error state.  Errors are cumulative;
     * once the stream encounters an error, this routine will return true on
     * all successive calls.
     *
     * @return True if the print stream has encountered an error, either on
     * the underlying output stream or during a format conversion.
     */
    public static boolean checkError() {
	return out.checkError();
    }

    /* Methods that do not terminate lines */

    /** Print a boolean. */
    public static void print( boolean b ) {
        if (debugLevel > EMPTY_LEVEL)  out.print( b );
    }

    /** Print a character. */
    public static void print( char c ) {
        if (debugLevel > EMPTY_LEVEL)  out.print( c );
    }

    /** Print an integer. */
    public static void print( int i ) {
        if (debugLevel > EMPTY_LEVEL)  out.print( i );
    }

    /** Print a long. */
    public static void print( long l ) {
        if (debugLevel > EMPTY_LEVEL)  out.print( l );
    }

    /** Print a float. */
    public static void print( float f ) {
        if (debugLevel > EMPTY_LEVEL)  out.print( f );
    }

    /** Print a double. */
    public static void print( double d ) {
        if (debugLevel > EMPTY_LEVEL)  out.print( d );
    }

    /** Print an array of chracters. */
    public static void print( char s[] ) {
        if (debugLevel > EMPTY_LEVEL)  out.print( s );
    }

    /** Print a String. */
    public static void print( String s ) {
        if (debugLevel > EMPTY_LEVEL)  out.print( s );
    }

    /** Print a String. */
    public static void print2( String s ) {
        if (debugLevel > SIMPLE_LEVEL)  out.print( s );
    }

    /** Print an object. */
    public static void print( Object obj ) {
        if (debugLevel > EMPTY_LEVEL)  out.print( obj );
    }

    /* Methods that do terminate lines */

    /** Finish the line. */
    public static void println() {
        if (debugLevel > EMPTY_LEVEL)  out.println();
    }

    /** Print a boolean, and then finish the line. */
    public static void println( boolean x ) {
        if (debugLevel > EMPTY_LEVEL)  out.println( x );
    }

    /** Print a character, and then finish the line. */
    public static void println( char x ) {
        if (debugLevel > EMPTY_LEVEL)  out.println( x );
    }

    /** Print an integer, and then finish the line. */
    public static void println( int x ) {
        if (debugLevel > EMPTY_LEVEL)  out.println( x );
    }

    /** Print a long, and then finish the line. */
    public static void println( long x ) {
        if (debugLevel > EMPTY_LEVEL)  out.println( x );
    }

    /** Print a float, and then finish the line. */
    public static void println( float x ) {
        if (debugLevel > EMPTY_LEVEL)  out.println( x );
    }

    /** Print a double, and then finish the line. */
    public static void println( double x ) {
        if (debugLevel > EMPTY_LEVEL)  out.println( x );
    }

    /** Print an array of characters, and then finish the line. */
    public static void println( char x[] ) {
        if (debugLevel > EMPTY_LEVEL)  out.println( x );
    }

    /** Print a String, and then finish the line. */
    public static void println( String x ) {
        if (debugLevel > EMPTY_LEVEL)  out.println( x );
    }
    /** Print a String. */
    public static void println2( String s ) {
        if (debugLevel > SIMPLE_LEVEL)  out.println( s );
    }

    /** Print an Object, and then finish the line. */
    public static void println( Object x ) {
        if (debugLevel > EMPTY_LEVEL)  out.println( x );
    }

    public static void write(byte[] buf,int off, int len){
      if(debugLevel > EMPTY_LEVEL) out.write(buf,off,len);
    }

    public static void write2(byte[] buf,int off, int len){
      if(debugLevel > SIMPLE_LEVEL) out.write(buf,off,len);
    }

}
