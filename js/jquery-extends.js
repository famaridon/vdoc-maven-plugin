$.fn.isEmpty = function () {
    return this.size() === 0;
}

$.fn.htoc = function (opts) {
    opts = $.extend({}, $.fn.htoc.defaults, opts);

    //
    var levels = opts.selectors.toLowerCase().replace('h', '').split(',').sort();
    var levelContainer = [];
    var itemContainer = [];

    var headingsContainer = $(opts.container);
    var headings = $(opts.selectors, headingsContainer);

    var prevLevel = levels[0];
    var newLevel = prevLevel;

    levelContainer[newLevel] = opts.addTocList(this, opts);

    $.each(headings, function (index, heading) {
        newLevel = parseInt(heading.tagName.toLowerCase().replace('h', ''));
        if (newLevel > prevLevel) {
            // start sub list
            levelContainer[newLevel] = opts.addTocList(itemContainer[prevLevel], opts);
            prevLevel = newLevel;
        } else if (newLevel < prevLevel) {
            // close sub list
            prevLevel--;
        }
        // add toc item to current level
        var anchor;
        if ($(heading).attr('id')) {
            anchor = $(heading).attr('id');
        } else {
            anchor = opts.anchorName($(heading), index);
            $(heading).attr('id', anchor);
        }

        itemContainer[newLevel] = opts.addTocItem(levelContainer[newLevel], $(heading), anchor);
    });

    return this;
};

$.fn.htoc.defaults = {
    container: 'body',
    tocListTemplate: '<ul></ul>',
    tocItemTemplate: '<li></li>',
    prefix: 'htoc',
    selectors: 'h1,h2,h3',
    addTocList: function (container) {
        var list = $(this.tocListTemplate);
        $(container).append(list);
        return list;
    },
    addTocItem: function (container, heading, anchor) {
        var item = $(this.tocItemTemplate);

        var link = $('<a href="#' + heading.attr('id') + '">' + heading.text() + '</a>');
        item.append(link);
        item.addClass(this.tocItemClass(heading));
        $(container).append(item);
        return item;
    },
    anchorName: function (heading, headingIndex) { //custom function for anchor name
        return this.prefix + headingIndex;
    },
    tocItemClass: function (heading) { // custom function for item class
        return 'htoc-item';
    }
};
