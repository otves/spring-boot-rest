window.ExampleDefaults = {
	width   : 1000,
	height  : 500,
    idCounter : 1
};

/* For testability purposes so that Siesta can record targets more easily, provide an id to all scheduler panels */
Sch.panel.TimelineGridPanel.override({
    constructor : function(config) {
        config = config || {};

        config.id = config.id  || (this.xtype + '-' + ExampleDefaults.idCounter++);

        this.callParent([config]);
    }
});

Sch.panel.TimelineTreePanel.override({
    constructor : function(config) {
        config = config || {};

        config.id = config.id  || (this.xtype + '-' + ExampleDefaults.idCounter++);

        this.callParent([config]);
    }
});
/* EOF For testability purposes so that Siesta can record targets more easily, provide an id to all scheduler panels */

Ext.onReady(function() {
    if (window.location.href.match(/^file:\/\/\//)) {
        Ext.Msg.alert('ERROR: You must use a web server', 'You must run the examples in a web server (not using the file:/// protocol)');
    }
});