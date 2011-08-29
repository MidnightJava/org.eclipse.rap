/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "org.eclipse.rwt.protocol" );

org.eclipse.rwt.protocol.EncodingUtil = {

  _escapeRegExp : /(&|<|>|\")/g,
  _escapeRegExpMnemonics : /(&&|&|<|>|")/g,
  _newlineRegExp : /(\r\n|\n|\r)/g,
  _outerWhitespaceRegExp : /(^ {1,1}| +$)/g,
  _whitespaceRegExp : / {2,}/g,

  _escapeResolver : null,
  _escapeResolverMnemonics : null,
  _mnemonicFound : false,

  _escapeMap : {
    "<" : "&lt;",
    ">" : "&gt;",
    "\"" : "&quot;",
    "&&" : "&amp;",
    "&" : "&amp;"
  },

  /**
   * Replaces all occurrences of the characters <,>,&," with their corresponding HTML entities. 
   * When the parameter mnemonic is set to true, this method handles ampersand characters in the 
   * text as mnemonics in the same manner as SWT does.
   * Note: In contrast to SWT, the characters following an ampersand are currently not underlined.
   *
   * @param text the input text
   * @param mnemonics if true, the function is mnemonic aware,
   *        otherwise all ampersand characters are directly rendered.
   * @return the resulting text
   */
  // Note [rst]: Single quotes are not escaped as the entity &apos; is not
  //             defined in HTML 4. They should be handled by this method once
  //             we produce XHTML output.
  escapeText : function( text, mnemonics ) {
    if( text === null ) {
      throw new Error( "escapeText with parameter null not allowed" );
    }
    var result;
    if( mnemonics ) {
      result = text.replace( this._escapeRegExpMnemonics, this._getEscapeResolverMnemonics() );
    } else {
      this._mnemonicFound = false; // first found mnemonic may be resolved
      result = text.replace( this._escapeRegExp, this._getEscapeResolver() );
    }
    return this.truncateAtZero( result );
  },

  truncateAtZero : function( text ) {
    var result = text;
    var index = result.indexOf( "\000" );
    if( index !== -1 ) {
      result = result.substring( 0, index );
    }
    return result;
  },

  /**
   * Replaces all newline characters in the specified input string with the
   * given replacement string. All common newline characters are replaced (Unix,
   * Windows, and MacOS).
   *
   * @param input the string to process
   * @param replacement the string to replace line feeds with.
   * @return a copy of the input string with all newline characters replaced
   */
  replaceNewLines : function( text, optionalReplacement ) {
    var replacement = arguments.length > 1 ? optionalReplacement : "\\n";
    return text.replace( this._newlineRegExp, replacement );
  },

  /**
   * Replaces white spaces in the specified input string with &nbsp;.
   * For correct word wrapping, the last white space in a sequence of white
   * spaces is not replaced, if there is a different character following.
   * A single white space between words is not replaced whereas a single
   * leading white space is replaced.
   *
   * @param input the string to process
   * @return a copy of the input string with white spaces replaced
   */  
  replaceWhiteSpaces : function( text ) {
    var result = text.replace( this._outerWhitespaceRegExp, this._outerWhitespaceResolver );
    result = result.replace( this._whitespaceRegExp, this._whitespaceResolver );
    return result;
  },

  /////////
  // Helper
  
  _getEscapeResolverMnemonics : function() { 
    if( this._escapeResolverMnemonics ===  null ) {
      this._getEscapeResolver(); // implicitly create default resolver
      var util = this;
      this._escapeResolverMnemonics = function( match ) {
        var result;
        if( match === "&" && !util._mnemonicFound ) {
          result = "";
          util._mnemonicFound = true;
        } else {
          result = util._escapeResolver( match );
        }
        return result;
      };
    }
    return this._escapeResolverMnemonics;
  },

  _getEscapeResolver : function() {
    if( this._escapeResolver === null ) {
      var util = this;
      this._escapeResolver = function( match ) {
        return util._escapeMap[ match ];
      };
    }
    return this._escapeResolver;
  },

  _outerWhitespaceResolver : function( match ) {
    return match.replace( / /g, "&nbsp;" );
  },

  _whitespaceResolver : function( match ) {
    return match.slice( 1 ).replace( / /g, "&nbsp;" ) + " ";
  }

};
