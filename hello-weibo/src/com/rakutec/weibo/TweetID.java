package com.rakutec.weibo;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;

@Entity
public class TweetID {
    @Id
    public String userId;

    @Basic
    public Long latestId;

    public TweetID(String userId) {
        this.userId = userId;
    }

    public void update(Long tweetId) {
        System.out.println("updating latest id to " + tweetId);
        EntityManager em = EMFService.get().createEntityManager();
        try {
            em.getTransaction().begin();
            this.latestId = tweetId;
            em.merge(this);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public static TweetID loadTweetID(String user) {
        EntityManager em = EMFService.get().createEntityManager();
        TweetID tid;
        try {
            tid = em.find(TweetID.class, user);
            if (tid == null) {
                System.out.println("TID NOT FOUND");
                tid = new TweetID(user);
                tid.latestId = (long) 0;
                em.persist(tid);
            } else {
                System.out.println("FOUND TID:" + tid.latestId);
            }
        } finally {
            em.close();
        }
        return tid;
    }
}
