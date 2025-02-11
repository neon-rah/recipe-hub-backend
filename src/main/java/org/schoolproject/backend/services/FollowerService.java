package org.schoolproject.backend.services;

import org.schoolproject.backend.entities.Follower;

import java.util.List;
import java.util.UUID;

public interface FollowerService {
void followUser(UUID followerId, UUID followedId);
void unfollowUser(UUID followerId, UUID followedId);
boolean isFollowing(UUID followerId, UUID followedId);
List<Follower> getFollowers(UUID userId);
List<Follower> getFollowing (UUID userId);
int getFollowerCount(UUID userId);
int getFollowingCount(UUID userId);
}
