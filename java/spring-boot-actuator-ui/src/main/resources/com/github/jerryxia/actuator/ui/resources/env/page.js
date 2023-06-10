const dashboardPathPrefix = $('#hid_val').data('dashboardpathprefix');

let app = new Vue({
	el: '#app',
	data: {
		dashboardPathPrefix: dashboardPathPrefix,
		profiles: [],
		systemEnvironment: [],
		systemProperties: [],
		applicationConfigs: []
	},
	computed: {
		logCount: function () {
			let that = this;
			return 0;
		}
	},
	created: function () {
		console.info('created');
		this.listEnv();
	},
	updated: function () {
		console.info('updated');
	},
	methods: {
		listEnv: function () {
			let that = this;
			$.get(dashboardPathPrefix + '/env', {}, function (res) {
				for (let k in res) {
					switch (k) {
						case 'systemEnvironment':
							that.systemEnvironment = that.sortObjectToArray(res.systemEnvironment);
							break;
						case 'systemProperties':
							that.systemProperties = that.sortObjectToArray(res.systemProperties);
							break;
						case 'profiles':
							that.profiles = res.profiles;
							break;
						default:
							if (k.indexOf('.properties') > -1) {
								that.applicationConfigs.push({ key: k, value: that.sortObjectToArray(res[k]) });
							} else {
								console.log(k);
								console.log(res[k]);
							}
							break;
					}
				}
			});
		},
		sortObjectToArray: function (originObj) {
			var result = [];
			var objKeys = Object.keys(originObj).sort();
			for (var i = 0; i < objKeys.length; i++) {
				result.push({
					key: objKeys[i],
					value: originObj[objKeys[i]]
				});
			}
			return result;
		}
	}
});
