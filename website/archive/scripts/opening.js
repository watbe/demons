/*                                                                          74demo.js
-------------------------------------------------------------------------------------
Purpose
~~~~~~~
Demo of use of JS file for students

Designed Sep 09
~~~~~~~~ Tim

Modified date
~~~~~~~~ name

-----------------------------------------------------------------------------------*/
// Initialisation
// ~~~~~~~~~~~~~~

// Constants (Declare at the top so they becomes global)
// ~~~~~~~~~

// Global variables
// ~~~~~~~~~~~~~~~~
    var bigWin  // (almost) full screen window for display of work

//------------------------------------------------------------------------

// Subroutines
// ~~~~~~~~~~~

//---------------------------------------------------------
// Main functions
// ~~~~~~~~~~~~~~
// Open a big window (as much of the screen as possible) for display of work
//
// Note: call this function from an event
// e.g. <a href="testContent.htm" onclick="return fullOpen('testContent.htm')">
//
function fullOpen(uri) {
    var fullLeft = 0 ; // position of new window
    var fullTop = 0 ;
    var windowOptions = new String ;

    if (typeof(screen) == 'object') {
        fullWidth = screen.availWidth - 12 ;   // Fit on screen
        fullHeight = screen.availHeight - 50 ; // allowing for window furniture
    } else {
        fullWidth = 1012 ;
        fullHeight = 718
    }
    // Note: don't put spaces in the quoted option list for window.open
    windowOptions = 'toolbar=no,location=no,directories=no' +
                    ',status=no' +
                    ',menubar=no' +
                    ',scrollbars=no' +
                    ',resizable=no,alwaysRaised=yes,dependent=no' +
                    ',width=' + fullWidth +
                    ',height=' + fullHeight +
                    ',left=' + fullLeft +
                    ',top=' + fullTop ;

    closeWin(bigWin) ; // Avoid possibility of loading page into minimised window
    bigWin = window.open(uri, 'FullScreen', windowOptions)
    bigWin.focus() ;

    return false // Return false so any HREF in the calling document is not navigated to
}
// ----------------------------------------------------------------------------------------------

// ------------------------------------------------------------------------------------------------
