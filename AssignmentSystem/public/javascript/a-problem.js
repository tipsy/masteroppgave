
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

    $(".hidden-when-editor-maximized").collapsible(); //makes ever header in this div collapsible

    $(".collapsing-header").click(function(){
        $(this).find("i").toggleClass("fa-angle-down fa-angle-right");
    });

    var annotationList = [];
    annotationList.push( new Annotation(1, "This is an error", "error") );
    annotationList.push( new Annotation(2, "This is a warning", "warning") );
    annotationList.push( new Annotation(3, "This is information", "info") );
    updateAnnotations(editors[0], annotationList);

});

function createEditor(editorID){
    var editor = ace.edit(editorID);
    editor.setTheme("ace/theme/eclipse");
    editor.getSession().setMode("ace/mode/java");
    return editor;
}

function updateAnnotations(editor, annotations){
    editor.getSession().setAnnotations(annotations);
}

function Annotation(lineNumber, message, type){
    this.row = lineNumber;
    this.text = message;
    this.type = type; // "error", "warning", "info"
}