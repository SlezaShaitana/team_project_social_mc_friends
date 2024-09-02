package com.social.mc_friends.repository.specificftions;

import com.social.mc_friends.dto.StatusCode;
import com.social.mc_friends.model.Relationship;
import com.social.mc_friends.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class FriendsSpecifications {
    public static Specification<Relationship> operationIdEquals(UUID uuid){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("statusChangeId"), uuid));
    }
    public static Specification<Relationship> friendIsDelete(String status){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("statusCode"), String.format("%%%s%%", status)));
    }
    public static Specification<Relationship> friendIdFromEquals(UUID uuid){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), uuid));
    }
//    public static Specification<Relationship> friendsStatusEquals(StatusCode status){
//        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("statusCode"), String.format("%%%s%%", status)));
//    }
public static Specification<Relationship> friendsStatusEquals(StatusCode status){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("statusCode"), status));
    }
    public static Specification<Relationship> friendIdToEquals(UUID uuid){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("relatedUserId"), uuid));
    }
//    public static Specification<Relationship> friendsPreviousStatusEquals(StatusCode previousStatus){
//        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("previousStatusCod"), String.format("%%%s%%", previousStatus)));
//    }
    public static Specification<Relationship> friendsPreviousStatusEquals(StatusCode previousStatus){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("previousStatusCod"), previousStatus));
    }
}
