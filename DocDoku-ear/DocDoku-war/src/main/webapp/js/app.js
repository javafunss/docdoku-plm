var app = {
	init: function (config) {
		this.workspaceId = config.workspaceId
		this.login = config.login

		$('.collapse').collapse();
		$('.dropdown-toggle').dropdown();
		$('.modal').modal();

		rootFolderView = new FolderView({model:new RootFolder()});
		$("#folders").append(rootFolderView.el);
	},
	dirname: function (path) {
		return path.replace(/\/[^\/]*$/g, '');
	},
	basename: function (path) {
		return path.replace(/^.*\//g, '');
	},
	// Returns the rest of the given path
	restpath: function(completePath) {
		var path = completePath.split("/");
		path.shift();
		return path.join("/");
	},
	i18n: {}
}
