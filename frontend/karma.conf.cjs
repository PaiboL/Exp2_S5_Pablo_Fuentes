module.exports = config => config.set({
 frameworks: ['jasmine'], plugins: [require('karma-jasmine'),require('karma-chrome-launcher'),require('karma-coverage')],
 reporters: ['progress'], browsers: ['ChromeHeadlessNoSandbox'],
 customLaunchers: {ChromeHeadlessNoSandbox: {base:'ChromeHeadless',flags:['--no-sandbox','--disable-dev-shm-usage']}},
 singleRun: true
});
