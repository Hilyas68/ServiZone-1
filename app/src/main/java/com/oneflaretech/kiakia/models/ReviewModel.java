package com.oneflaretech.kiakia.models;

import java.util.List;

/**
 * Created by kolawoleadewale on 10/7/17.
 */

public class ReviewModel {

    /**
     * current_page : 1
     * data : [{"id":4,"user_id":"10","expert_id":"1","rating":"4","message":"sdferf","updated_at":"2017-10-16 22:41:06","created_at":"2017-10-16 22:41:06","user":{"id":10,"role_id":"2","avatar":"/finco-data/users/sxcv-1508095186.png","name":"sxcv","age":"223","gender":"zxcv b","mobile":"73384282","about":"asdfgbgfds","profession_id":"2","email":"aaaaaabbbbbb","address":"sdfbgfds","longitude":"0.0","latitude":"0.0","type":"expert","status":"pending","is_blocked":"0","updated_at":"2017-10-15 19:19:46","created_at":"2017-10-15 19:01:01"}},{"id":5,"user_id":"10","expert_id":"1","rating":"3","message":"cvhjjhg","updated_at":"2017-10-17 03:34:46","created_at":"2017-10-17 03:34:46","user":{"id":10,"role_id":"2","avatar":"/finco-data/users/sxcv-1508095186.png","name":"sxcv","age":"223","gender":"zxcv b","mobile":"73384282","about":"asdfgbgfds","profession_id":"2","email":"aaaaaabbbbbb","address":"sdfbgfds","longitude":"0.0","latitude":"0.0","type":"expert","status":"pending","is_blocked":"0","updated_at":"2017-10-15 19:19:46","created_at":"2017-10-15 19:01:01"}}]
     * from : 1
     * last_page : 1
     * next_page_url : null
     * path : http://servizone.net/api/reviews
     * per_page : 40
     * prev_page_url : null
     * to : 2
     * total : 2
     */

    public int current_page;
    public int from;
    public int last_page;
    public String next_page_url;
    public String path;
    public int per_page;
    public String prev_page_url;
    public int to;
    public int total;
    public List<Data> data;

    public static class Data {
        /**
         * id : 4
         * user_id : 10
         * expert_id : 1
         * rating : 4
         * message : sdferf
         * updated_at : 2017-10-16 22:41:06
         * created_at : 2017-10-16 22:41:06
         * user : {"id":10,"role_id":"2","avatar":"/finco-data/users/sxcv-1508095186.png","name":"sxcv","age":"223","gender":"zxcv b","mobile":"73384282","about":"asdfgbgfds","profession_id":"2","email":"aaaaaabbbbbb","address":"sdfbgfds","longitude":"0.0","latitude":"0.0","type":"expert","status":"pending","is_blocked":"0","updated_at":"2017-10-15 19:19:46","created_at":"2017-10-15 19:01:01"}
         */

        public int id;
        public String user_id;
        public String expert_id;
        public String rating;
        public String message;
        public String updated_at;
        public String created_at;
        public User user;

        public static class User {
            /**
             * id : 10
             * role_id : 2
             * avatar : /finco-data/users/sxcv-1508095186.png
             * name : sxcv
             * age : 223
             * gender : zxcv b
             * mobile : 73384282
             * about : asdfgbgfds
             * profession_id : 2
             * email : aaaaaabbbbbb
             * address : sdfbgfds
             * longitude : 0.0
             * latitude : 0.0
             * type : expert
             * status : pending
             * is_blocked : 0
             * updated_at : 2017-10-15 19:19:46
             * created_at : 2017-10-15 19:01:01
             */

            public int id;
            public String role_id;
            public String avatar;
            public String name;
            public String age;
            public String gender;
            public String mobile;
            public String about;
            public String profession_id;
            public String email;
            public String address;
            public String longitude;
            public String latitude;
            public String type;
            public String status;
            public String is_blocked;
            public String updated_at;
            public String created_at;
        }
    }
}
