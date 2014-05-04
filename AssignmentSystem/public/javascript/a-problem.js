
$(document).ready(function () {

    var editors = [];

    $(".ace-editor-instance").each(function(){
        editors.push( createEditor( $(this).attr("id") ));
    });

    $("#ae-toggle-fullscreen").click(function(){
        $("#ace-editor-wrapper").toggleClass("maximized");
        $("#ae-toggle-fullscreen").toggleClass("fa-expand fa-compress");
        $(editors).each(function() { this.resize(); });
        setupResizeableEditor(editors);
    });

    $(".ae-theme-settings").click(function(){
        var theme = ("ace/theme/"+$(this).attr("id"));
        $(editors).each(function() { this.setTheme(theme); });
    });

    setupResizeableEditor(editors);

});

function createEditor(editorID){
    var editorID = ace.edit(editorID);
    editorID.setTheme("ace/theme/eclipse");
    editorID.getSession().setMode("ace/mode/java");
    return editorID;
}

function setupResizeableEditor(editors) {
    $(function () {
        $(".ace-editor-instance").resizable({
            handles: 's',
            maxHeight: $("#ace-editor-wrapper").height()-100,
            resize: function (e, ui) {
                var parent = ui.element.parent();
                var remainingSpace = parent.height() - ui.element.outerHeight(),
                    divTwo = ui.element.next(),
                    divTwoHeight = (remainingSpace - (divTwo.outerHeight() - divTwo.height())) / parent.height() * 100 + "%";
                divTwo.height(divTwoHeight);
                $(editors).each(function() { this.resize(); });
            },
            stop: function (e, ui) {
                var parent = ui.element.parent();
                ui.element.css({
                    height: ui.element.height() / parent.height() * 100 + "%"
                });
            }
        });
    });
}