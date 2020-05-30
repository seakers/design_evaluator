package vassar;



//          ____   ____                                     ______  __    _                  _
//         |_  _| |_  _|                                  .' ___  |[  |  (_)                / |_
//           \ \   / /,--.   .--.   .--.   ,--.   _ .--. / .'   \_| | |  __  .---.  _ .--. `| |-'
//            \ \ / /`'_\ : ( (`\] ( (`\] `'_\ : [ `/'`\]| |        | | [  |/ /__\\[ `.-. | | |
//             \ ' / // | |, `'.'.  `'.'. // | |, | |    \ `.___.'\ | |  | || \__., | | | | | |,
//              \_/  \'-;__/[\__) )[\__) )\'-;__/[___]    `.____ .'[___][___]'.__.'[___||__]\__/


import vassar.database.DatabaseClient;
import vassar.jess.JessEngine;

public class VassarClient {

    private JessEngine engine;

    public static class Builder {

        private JessEngine engine;

        public Builder() {

        }

        public Builder setEngine(JessEngine engine) {
            this.engine = engine;
            return this;
        }

        public VassarClient build() {
            VassarClient build = new VassarClient();
            build.engine = this.engine;
            return build;
        }

    }




}
