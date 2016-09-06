/* global App */

Ext.application({
    name      : 'App',
    appFolder : 'js/App',

    views     : [
        'Viewport'
    ],

    stores     : [
        'EventStore',
        'ResourceStore'
    ],

    mainView : {
        xtype : 'mainviewport'
    }
});

