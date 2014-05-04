$(document).ready(function () {

    $(".assignment-preview a").find(".progress-bar").each(function(){
        scoreAdjust( getPercentWidth($(this)), $(this), 30+Math.random()*70);
    });

    var alwaysMax = $(".assignment-preview a").first().find(".progress-bar");
    scoreAdjust( getPercentWidth(alwaysMax), alwaysMax, 100);

});

function getPercentWidth(e){
    return Math.round( e.width()/e.parent().width() * 100 );
}

function scoreAdjust (i, progressBar, targetScore) {
    setTimeout(function () {
        if(i === 50){ progressBar.parent().parent().find(".fa-star.bronze, .glow.bronze").addClass("star-earned") }
        if(i === 75){ progressBar.parent().parent().find(".fa-star.silver, .glow.silver").addClass("star-earned") }
        if(i === 100){progressBar.parent().parent().find(".fa-star.gold,   .glow.gold"  ).addClass("star-earned") }
        progressBar.width(i+"%");
        i++;
        if (i <= targetScore) {
            scoreAdjust(i, progressBar, targetScore);
        }
    }, 20)
}

