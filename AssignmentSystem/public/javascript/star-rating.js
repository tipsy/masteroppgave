$(document).ready(function () {

    var score = 100;

    $(".assignment-preview a").find(".progress-bar").each(function(){
        scoreAdjust( getPercentWidth($(this)), $(this) );
    });

});

function getPercentWidth(e){
    var width = e.width();
    var parentWidth = e.parent().width();
    var percent = Math.round( 100*width/parentWidth );
    return percent;
}

function scoreAdjust (i, progressBar) {
    setTimeout(function () {
        if(i === 50){ progressBar.parent().parent().find(".fa-star.bronze, .glow.bronze").addClass("star-earned") }
        if(i === 75){ progressBar.parent().parent().find(".fa-star.silver, .glow.silver").addClass("star-earned") }
        if(i === 100){progressBar.parent().parent().find(".fa-star.gold,   .glow.gold"  ).addClass("star-earned") }
        progressBar.width(i+"%");
        i++;
        if (i < 101) {
            scoreAdjust(i, progressBar);
        }
    }, 20)
}

