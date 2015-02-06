var updateDotCiBuildHistory = function () {
    var buildHistoryTable = $($(".build-history-table").filter(":visible"))
    var firstBuildNumber = buildHistoryTable.attr('data-first-build-number');
    var lastBuildNumber = buildHistoryTable.attr('data-last-build-number');
    jQuery.ajax({
        url: "buildHistory/ajax?firstBuildNumber=" + firstBuildNumber + "&lastBuildNumber=" + lastBuildNumber
    }).done(function (data) {
        jQuery(data).select("tr")
            .each(function (index) {
                var newRow = jQuery(this)
                var rowId = newRow.attr("id")
                var currentRow = jQuery("#" + rowId)
                if (currentRow.length > 0) {
                    currentRow.replaceWith(newRow.prop('outerHTML'));
                } else {
                    buildHistoryTable.prepend(newRow.prop('outerHTML'));
                }
            });
    });
};

var addNewDotCiTab = function () {
    bootbox.prompt("Branch Regex", function (result) {
        if (result != null) {
            window.location.href = 'buildHistory/addTab?tab=' + result;
        }
    });
};

var dotCiRemoveTab = function(e) {
    e.preventDefault();
    e.stopPropagation();
    var tab = jQuery(e.target).attr("data-name");
    window.location.href = 'buildHistory/removeTab?tab=' + tab;
};


function dotCiLoadTab(tab) {
    var $this = jQuery(tab),
        loadurl = $this.attr('href'),
        targ = $this.attr('data-target');

    jQuery.get(loadurl, function (data) {
        jQuery(targ).html(data);
    });
}


function dotCiCancelBuild(e,url){
    e.preventDefault();
    $.post(url);
    return false;
}
jQuery(document).ready(function ($) {
    // setInterval(updateDotCiBuildHistory, 1000 * 5);//Every 5 seconds
    jQuery("#addNewTab").click(addNewDotCiTab);
    jQuery(".removeTab").click(dotCiRemoveTab);
    $('a[data-toggle="tab"]').on('shown.bs.tab',  function(e){
        dotCiLoadTab(this)
        return false;})
    var activeTab = $('a[data-toggle="tab"]').filter(function(a){return $(this).parent().hasClass('active')});
    dotCiLoadTab(activeTab);
});

function dotciGoto(e,x){
    e.preventDefault();
    window.location =  x;
    return false;
}
$(function(){
     DotCi = Backbone.Model.extend({
        initialize: function(){
            alert("Oh hey! ");
        },
        defaults: {
            name: 'Default title',
            releaseDate: 2011
        }
    });


    var UserProjectsView = Backbone.View.extend({
        el: '#recent-user-projects',
        template: _.template($('#item-template').html()),
        initialize: function(){
            this.render();
        },
        render: function(){
            this.$el.html(this.template({title: 'world!'}));
        }
    });
    var appView = new UserProjectsView();

})





