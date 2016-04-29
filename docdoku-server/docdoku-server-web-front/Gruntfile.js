'use strict';

module.exports = function (grunt) {

    var config = {
        clean: {
            options: {
                force: true
            },
            dist: ['.tmp', 'dist']
        },
        cssmin: {
            options: {
                keepSpecialComments: 0
            }
        },
        copy:{},
        less:{},
        usemin:{},
        htmlmin:{},
        requirejs:{},
        uglify:{}
    };

    require('time-grunt')(grunt);
    require('load-grunt-tasks')(grunt);

    function initModule(module){
        module.loadConf(config, grunt);
        module.loadTasks(grunt);
        return module;
    }

    initModule(require('./grunt/dev/server-front'));
    initModule(require('./grunt/dev/tests'));
    initModule(require('./grunt/dev/jshint'));
    initModule(require('./grunt/tasks/copy'));
    initModule(require('./grunt/tasks/build'));
    initModule(require('./grunt/tasks/deploy'));
    initModule(require('./grunt/modules/workspace-management'));
    initModule(require('./grunt/modules/account-management'));
    initModule(require('./grunt/modules/document-management'));
    initModule(require('./grunt/modules/change-management'));
    initModule(require('./grunt/modules/product-management'));
    initModule(require('./grunt/modules/product-structure'));
    initModule(require('./grunt/modules/visualization'));
    initModule(require('./grunt/modules/download'));

    grunt.initConfig(config);

    grunt.registerTask('default',['jshint','build']);

};
