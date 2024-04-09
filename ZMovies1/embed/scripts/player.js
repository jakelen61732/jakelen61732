var urlParams = new URLSearchParams(window.location.search);
var idFromUrl = urlParams.get("id");
var season = urlParams.get("s");
var episode = urlParams.get("e");
if (idFromUrl) {
    getDetails(idFromUrl);
}

function getDetails(idFromUrl) {
    const options = {
        method: 'GET',
        headers: {
            accept: 'application/json',
            Authorization: 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1MjFlNzMyM2FkY2E5MzVmOTFlYTc0MzQzOTJhNzA2YyIsInN1YiI6IjYzMjcyODQ2YmJkMGIwMDA4MjE3M2E0OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.ET9dcYHU9b_aza5jMmWqjtDhspF-8I6N2okHsQWw780'
        }
    };

    var elements = document.getElementsByClassName("loading");

    var isTv
    if (season && episode) {
        isTv = 'tv';
    } else {
        isTv = 'movie';
    }

    fetch('https://api.themoviedb.org/3/' + isTv + '/' + idFromUrl + '?language=en-US', options)
        .then(response => response.json())
        .then(response => {
            var name = response.original_name;
            if (season && episode) {
                document.title = response.original_name + ' Season ' + season + ' Episode ' + episode + " - Philippines Multi Player";
            } else {
                document.title = response.original_title + " - Philippines Multi Player";
            }
            $('meta[name="description"]').attr("content", response.overview);
            var imageUrl = 'https://image.tmdb.org/t/p/original' + response.backdrop_path;
            $('meta[property="og:image"]').attr("content", imageUrl);
            for (var i = 0; i < elements.length; i++) {
                elements[i].style.backgroundImage = 'url(' + imageUrl + ')';
            }
        })
        .catch(err => console.error(err));

}

function myFunction(position) {
    switch (position) {
        case 0:
            if (season && episode) {
                go('https://moviesapi.club/tv/' + idFromUrl + '-' + season + '-' + episode);
            } else {
                go('https://moviesapi.club/movie/' + idFromUrl);
            }
            break;
        case 1:
            if (season && episode) {
                go('https://vidsrc.me/embed/tv?tmdb=' + idFromUrl + '&season=' + season + '&episode=' + episode);
            } else {
                go('https://vidsrc.me/embed/movie?tmdb=' + idFromUrl);
            }
            break;
        case 2:
            if (season && episode) {
                go('https://vidsrc.to/embed/tv/' + idFromUrl + '/' + season + '/' + episode);
            } else {
                go('https://vidsrc.to/embed/movie/' + idFromUrl);
            }
            break;
        case 3:
            if (season && episode) {
                go('https://multiembed.mov/?video_id=' + idFromUrl + '&tmdb=1&s=' + season + '&e=' + episode);
            } else {
                go('https://multiembed.mov/?video_id=' + idFromUrl + '&tmdb=1');
            }
            break;
        case 4:
            if (season && episode) {
                go('https://player.smashy.stream/tv/' + idFromUrl + '?s=' + season + '&e=' + episode);
            } else {
                go('https://player.smashystream.com/movie/' + idFromUrl);
            }
            break;
    }
}

function go(loc) {
    document.getElementById('vsrcs').src = loc;
}

var header = document.getElementById("vidsrcs");
var btns = header.getElementsByClassName("btn-iframe");
for (var i = 0; i < btns.length; i++) {
    btns[i].addEventListener("click", function () {
        var current = document.getElementsByClassName("active");
        current[0].className = current[0].className.replace(" active", "");
        this.className += " active";
    });
}
/////////////////////////////////////////
$(".playbtnx").click(function () {
    var iframe = $("#vsrcs");
    iframe.src = myFunction(0);
    $("#vidsrcs").show();
});
//////////////////////////
function bgImage() {
    var x = document.getElementById("bgImage");
    if (x.style.display === "none") {
        x.style.display = "block";
    } else {
        document.getElementById("bgImage").remove();
        //    x.style.display = "none";
        $(".playbtnx").hide();
        var element = document.getElementById("embed-player");
        element.classList.toggle("animate-enter");
    }
}
/////////////////////////////////
function toggleBar() {
    var x = document.getElementById("vidsrcs");
    if (x.style.display === "none") {
        x.style.display = "block";
        document.getElementById('vsrcs').style.height = "90%";
        $(".showbar").hide();
    } else {
        x.style.display = "none";
        document.getElementById('vsrcs').style.height = "100%";
        $(".showbar").show();
    }
}
