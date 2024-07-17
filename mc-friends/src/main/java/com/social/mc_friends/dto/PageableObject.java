package com.social.mc_friends.dto;

import lombok.Data;

@Data
public class PageableObject {
   private int offset;
   private Sort sort;
   private int pageSize;
   private boolean paged;
   private boolean unPaged;
   private  int pageNumber;

}
