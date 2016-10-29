/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

(function () {
    this.app = angular.module("kiosk", [
        "ngRoute"
    ]);

    this.app.controller("KioskController", function ($scope) {
        $scope.isOccupied = true;
        $scope.resource = {
          name: "Meeting Room A"
        };
    });

    this.app.controller("TimeController", function ($scope, $timeout) {
        var tick = function () {
            $scope.currentTime = new Date();
            $timeout(tick, 1000);
        };

        tick();
    });

    this.app.controller("ReservationsController", function ($scope, $timeout) {
        //TODO: timeout to check for new reservations every minute
        $scope.reservations = [
            {
                title: "Get Seeded Planning",
                startTime: new Date("October 31, 2016 8:00"),
                endTime: new Date("October 31, 2016 9:00"),
                past: true
            },
            {
                title: "Hashtaggy Meeting",
                startTime: new Date("October 31, 2016 12:00"),
                endTime: new Date("October 31, 2016 13:00")
            },
            {
                title: "AptiTekk Sales Pitch",
                startTime: new Date("October 31, 2016 14:00"),
                endTime: new Date("October 31, 2016 15:30")
            }
        ];
    });

})();


