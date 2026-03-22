import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const isLoggedIn = computed(() => !!token.value)

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('admin_token', newToken)
  }

  function logout() {
    token.value = ''
    localStorage.removeItem('admin_token')
    window.location.href = '/#/login'
  }

  return { token, isLoggedIn, setToken, logout }
})