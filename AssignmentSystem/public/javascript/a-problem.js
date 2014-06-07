$(document).ready(function () {

    $("#ae-toggle-fullscreen").click(function(){
        $(".hidden-when-editor-maximized").toggle();
        $("#ace-editor-wrapper").toggleClass("maximized");
        $("#ae-toggle-fullscreen").toggleClass("fa-expand fa-compress");
        $(editors).each(function() { this.resize(); });
    });

    $(".ae-theme-settings").click(function(){
        var theme = ("ace/theme/"+$(this).attr("id"));
        $(editors).each(function() { this.setTheme(theme); });
    });

    $(".hidden-when-editor-maximized").collapsible(); //makes ever header in this div collapsible
    $(".collapsing-header").click(function(){
        $(this).find("i").toggleClass("fa-angle-down fa-angle-right"); //toggle icon on header-click
    });

});