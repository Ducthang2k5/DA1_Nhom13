package com.example.abc.authorization.data;

import com.example.abc.authorization.data.model.User;

public final class UserFactory {
   private static User currentUser = null;

   public static void signIn(User user) {
      currentUser = user;
   }

   public static User getCurrentUser() {
      return currentUser;
   }
}
