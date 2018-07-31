var app = angular.module('report',['checklist-model']);

app.controller('ReportFields', function($scope, $http) {
        $scope.data = {
            model: null,
            productAttri: [],
            packagingAttri: []
        };
        $scope.downloadfileshow = false;

        $http.get("http://localhost:8099/listbusinessunits").then(function (response) {
            $scope.data.bu = response.data;
        });

        $http.get("http://localhost:8099/listattributes/ProductAttributes").then(function (response) {
            console.log("prod attri " + response.data);
            $scope.data.productAttributes = response.data;
        })

    $http.get("http://localhost:8099/listattributes/PackagingAttributes").then(function (response) {
        console.log("pack attri " + response.data);
        $scope.data.packagingAttributes = response.data;
    })

    $scope.submit = function() {
        console.log($scope.data.productAttri);
        $http({
            method: 'POST',
            url: "http://localhost:8099/generateReport/"+$scope.data.model+"/?productcolumns=" + $scope.data.productAttri + "&packagecolumns=" + $scope.data.packagingAttri,

        }).then(function successCallback(response) {
            console.log("SUCCESS");
        }, function errorCallback(response) {
            console.log("ERROR" + response.toString());
        });


        $scope.downloadfileshow = true;
        // if(document.getElementById('downloadlink') != null) {
        //     document.getElementById('downloadlink').remove();
        // }
        // $scope.downloadUrl = "http://localhost:8099/download/"+$scope.data.model;
        // var newEle = angular.element("<div class='files' id='downloadlink'><a href='http://localhost:8099/download/data.model'>Download File</a> </div>");
        // var target = document.getElementById('downloadFile');
        // angular.element(target).append(newEle);

    };

        $scope.reset = function() {
            $scope.data.model = null;
            $scope.showproducts = false;
            $scope.showpackagings = false;
            $scope.checkall = false;
            $scope.downloadfileshow = false;
            if(document.getElementById('downloadlink') != null) {
                document.getElementById('downloadlink').remove();
            }
        }
    });



if('should check ngShow', function() {
    var checkbox = element(by.model('showproducts'));
    var checkElem = element(by.css('#productattr'));

    expect(checkElem.isDisplayed()).toBe(false);
    checkbox.click();
    expect(checkElem.isDisplayed()).toBe(true);
});

