var app = angular.module('process',['checklist-model']);

app.controller('renderBGP', function($scope, $http, $sce) {
    $scope.data = {
        inboundContents : null,
        outboundContents : null,
        bgpContents : null,
        inboundselect : null,
        outboundselect : null,
        bgpcheckbox : null,
        inboundcheckbox : null,
        outboundcheckbox : null
    };
    $scope.showinbound = false;
    $scope.showoutbound = false;
    $scope.showbgp = false;
    $scope.downloadfileshow = false;
    $scope.showinbounddiv = false;
    $scope.showoutbounddiv = false;
console.log("showbgp : " + $scope.showbgp);
    $scope.getHtml = function(html){
        return $sce.trustAsHtml(html);
    };

    $http.get("http://localhost:8099/getInboundReport").then(function (response) {
        var r = response.data.toString();
        $scope.data.inboundContents = r;

    });

    $http.get("http://localhost:8099/getOutboundReport").then(function (response) {
        var r = response.data.toString();
        $scope.data.outboundContents = r;

    });

    $http.get("http://localhost:8099/getBGPPerformanceReport").then(function (response) {
        var r = response.data.toString();
        $scope.data.bgpContents = r;

    });

    $scope.toggleOutboundDiv = function() {
        $scope.showoutbounddiv = !$scope.showoutbounddiv;
    }

    $scope.toggleInboundDiv = function() {
        $scope.showinbounddiv = !$scope.showinbounddiv;
    }

    $scope.toggleOutboundInnerDiv = function() {
        $scope.showoutbound = true;
    }

    $scope.toggleInboundInnerDiv = function() {
        $scope.showinbound = true;
    }

    $scope.toggleBgpDiv = function() {
        console.log("Entreging toggle bgp");
        $scope.showbgp = !$scope.showbgp;
    }

});

app.filter('html', function($sce) {
    return function(val) {
        return $sce.trustAsHtml(val);
    };
});