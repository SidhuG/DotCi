/*
The MIT License (MIT)

Copyright (c) 2014, Groupon, Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
package com.groupon.jenkins.github.services;

import com.groupon.jenkins.mongo.MongoRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import hudson.util.Secret;
import java.io.IOException;
import org.mongodb.morphia.Datastore;

import javax.inject.Inject;

public class GithubAccessTokenRepository extends MongoRepository {

    public static final String COLLECTION_NAME = "github_tokens";

    @Inject
    public GithubAccessTokenRepository(Datastore datastore) {
        super(datastore);
    }

    public String getAccessToken(String repourl) {
        DBObject token = getToken(repourl);
        return Secret.fromString(token.get("access_token").toString()).getPlainText();
    }

    public String getAssociatedLogin(String repoUrl) {
        DBObject token = getToken(repoUrl);
        return token.get("user").toString();
    }

    private DBObject getToken(String repourl) {
        BasicDBObject query = new BasicDBObject("repo_url", repourl);
        return getCollection().findOne(query);
    }

      public void put(String url,String accessToken, String user) throws IOException {
        DBObject token = getToken(url);

        String encryptedToken = getEncryptedValue(accessToken);

        if (token != null) {
            getCollection().remove(token);
        }

            BasicDBObject doc = new BasicDBObject("user", user).append("access_token", encryptedToken).append("repo_url", url);
            getCollection().insert(doc);
      }

    private String getEncryptedValue(String accessToken) {
        return Secret.fromString(accessToken).getEncryptedValue();
    }


    public void updateAccessToken(String username, String accessToken) {
        BasicDBObject query = new BasicDBObject("user", username);
        BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("access_token", getEncryptedValue(accessToken)));
        getCollection().update(query,update,false,true);
    }

    public boolean isConfigured(String url) {
        return getToken(url) != null;
    }

    protected DBCollection getCollection() {
        return getDatastore().getDB().getCollection(COLLECTION_NAME);
    }
}
