window.onload = function () {

    // Video
    var video = document.getElementById("video");

    // Buttons
    var playButton = document.getElementById("play-pause");
    var muteButton = document.getElementById("mute");
    var fullScreenButton = document.getElementById("full-screen");

    // Sliders
    var seekBar = document.getElementById("seek-bar");
    var volumeBar = document.getElementById("volume-bar");

    var videoTime = document.getElementsByClassName("video-time oswM");
    var videoLength = document.getElementsByClassName("video-duration oswM");

    var fduration = 0;
    if (!isNaN(video.duration)) {
        fduration = video.duration;
    }
    var fhours = Math.floor(fduration / (60 * 60));
    var fmins = "" + Math.floor(fduration / 60);
    var fseconds = "" + Math.floor(fduration - (fmins * 60));
    if (fmins.length == 1) {
        fmins = "0" + fmins;
    }
    if (fseconds.length == 1) {
        fseconds = "0" + fseconds;
    }
    videoLength[0].innerHTML = fhours + ':' + fmins + ":" + fseconds;


    // Event listener for the play/pause button
    playButton.addEventListener("click", function () {

        if (video.paused == true) {
            // Play the video
            video.play();

            // Update the button text to 'Pause'
            playButton.innerHTML = '<span class="ion-ios-pause"></span>';

        } else {
            // Pause the video
            video.pause();

            // Update the button text to 'Play'
            playButton.innerHTML = '<span class="ion-ios-play"></span>';
        }
    });


    // Event listener for the mute button
    muteButton.addEventListener("click", function () {
        if (video.muted == false) {
            // Mute the video
            video.muted = true;

            // Update the button text
            muteButton.innerHTML = '<span class="ion-ios-volume-low"></span>';
        } else {
            // Unmute the video
            video.muted = false;

            // Update the button text
            muteButton.innerHTML = '<span class="ion-ios-volume-high"></span>';
        }
    });


    // Event listener for the full-screen button
    fullScreenButton.addEventListener("click", function () {
        if (video.requestFullscreen) {
            video.requestFullscreen();
        } else if (video.mozRequestFullScreen) {
            video.mozRequestFullScreen(); // Firefox
        } else if (video.webkitRequestFullscreen) {
            video.webkitRequestFullscreen(); // Chrome and Safari
        }
    });


    // Event listener for the seek bar
    seekBar.addEventListener("change", function () {

        // Calculate the new time
        var time = video.duration * (seekBar.value / 100);
        // Update the video time
        video.currentTime = time;
    });

    // Update the seek bar as the video plays
    video.addEventListener("timeupdate", function () {
        // Calculate the slider value
        var value = (100 / video.duration) * video.currentTime;
        // Update the slider value
        seekBar.value = value;
        var hours = Math.floor(video.currentTime / (60 * 60));
        var mins = "" + Math.floor(video.currentTime / 60);
        var seconds = Math.floor(video.currentTime - (mins * 60));
        if (mins.length == 1) {
            mins = "0" + mins;
        }
        videoTime[0].innerHTML = hours + ':' + mins + ":" + seconds;
        if (!isNaN(video.duration)) {
            var rduration = video.duration - video.currentTime;
            var rhours = Math.floor(rduration / (60 * 60));
            var rmins = "" + Math.floor(rduration / 60);
            var rseconds = "" + Math.floor(rduration - (rmins * 60));
            if (rmins.length == 1) {
                rmins = "0" + rmins;
            }
            if (rseconds.length == 1) {
                rseconds = "0" + rseconds;
            }
            videoLength[0].innerHTML = rhours + ':' + rmins + ":" + rseconds;
        }
    });

    // Pause the video when the seek handle is being dragged
    seekBar.addEventListener("mousedown", function () {
        video.pause();
    });

    // Play the video when the seek handle is dropped
    seekBar.addEventListener("mouseup", function () {
        video.play();
    });

    // Event listener for the volume bar
    volumeBar.addEventListener("change", function () {
        // Update the video volume
        video.volume = volumeBar.value;
    });
}