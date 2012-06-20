$(document).ready(function() { 

	//If javascript is enabled, do the following initialisations
	$("#game").css('display', 'none');
	$("#start a").removeClass("no-js");

	//To animate the subtle fade effects of the play button
	$("#start a").mouseenter(function() {
		$(this).animate({
			"opacity": "1"
		}, 100);
	});

	$("#start a").mouseleave(function() {
		$(this).animate({
			"opacity": "0.5"
		}, 500);
	});

	//When play is pressed
	$("#start a").click(function() {

		$("#game").css('display', 'block');
		$("#applet").delay(500).css('margin-left', '0');
		$("#start").delay(1000).css('display', 'none');
		$("#introduction").delay(1000).css('display', 'none');
		$("#header h1 a").delay(1000).css('display', 'none');
	});
});