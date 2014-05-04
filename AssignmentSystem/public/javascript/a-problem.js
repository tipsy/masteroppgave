$(document).ready(function () {

    var editors = [];

    $(".ace-editor-instance").each(function(){
        editors.push( createEditor( $(this).attr("id") ));
    });

    $("#ae-toggle-fullscreen").click(function(){
        $("#ace-editor-wrapper").toggleClass("maximized");
        $("#ae-toggle-fullscreen").toggleClass("fa-expand fa-compress");
        $(editors).each(function() { this.resize(); });
    });

    $("#ae-set-github-theme").click(function(){
        $(editors).each(function() { this.setTheme("ace/theme/github"); });
    });

    $("#ae-set-eclipse-theme").click(function(){
        $(editors).each(function() { this.setTheme("ace/theme/eclipse"); });
    });


});

function createEditor(editorID){
    var editorID = ace.edit(editorID);
    editorID.setTheme("ace/theme/eclipse");
    editorID.getSession().setMode("ace/mode/java");
    return editorID;
}