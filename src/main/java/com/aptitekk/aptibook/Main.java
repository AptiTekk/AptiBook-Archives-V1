/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook;

public class Main {

    public static void main(String[] args) throws Exception {
        SwarmBuilder.buildSwarm().start().deploy(SwarmBuilder.buildDeployment());
    }

}
