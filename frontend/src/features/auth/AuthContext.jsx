import React, { createContext, useContext, useState } from 'react';
import { getToken, getUser, setToken, setUser, removeToken, removeUser } from '../../utils/token';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUserState] = useState(getUser());
  const [token, setTokenState] = useState(getToken());

  const login = (data) => {
    setToken(data.token);
    setTokenState(data.token);
    
    // Server response might vary, extracting essential info
    const userData = { email: data.email, fullName: data.fullName, role: data.role, userId: data.userId };
    setUser(userData);
    setUserState(userData);
  };

  const logout = () => {
    removeToken();
    removeUser();
    setTokenState(null);
    setUserState(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, isAuthenticated: !!token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
