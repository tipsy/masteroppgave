
$(document).ready(function () {

    var editors = [];

    $(".ace-editor-instance").each(function(){
        editors.push( createEditor( $(this).attr("id") ));
    });

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

    $(".hidden-when-editor-maximized").collapsible();
    $(".collapsing-header").click(function(){
        $(this).find("i").toggleClass("fa-angle-down fa-angle-right");
    });


});

function createEditor(editorID){
    var editorID = ace.edit(editorID);
    editorID.setTheme("ace/theme/eclipse");
    editorID.getSession().setMode("ace/mode/java");
    return editorID;
}

