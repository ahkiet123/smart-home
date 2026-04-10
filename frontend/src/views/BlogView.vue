<script setup>
import { computed, nextTick, onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const API_BASE =
  window.location.port === "5173" || window.location.port === "4173"
    ? "http://localhost:8080/api/v1"
    : "/api/v1";

const DEFAULT_THUMBNAIL =
  "https://images.unsplash.com/photo-1518005020951-eccb494ad742?w=900&q=80";
const pageSize = 12;

const blogs = ref([]);
const loading = ref(false);
const error = ref("");
const loaded = ref(false);
const searchQuery = ref("");
const currentSort = ref("date-desc");
const viewMode = ref("grid");
const currentPage = ref(0);
const isDetailOpen = ref(false);
const currentPost = ref(null);
const favorites = ref(
  JSON.parse(localStorage.getItem("blogFavorites") || "[]"),
);

function refreshIcons() {
  nextTick(() => {
    if (window.lucide) {
      window.lucide.createIcons();
    }
  });
}

function calculateReadingTime(text) {
  const wordsPerMinute = 200;
  const wordCount = String(text || "")
    .trim()
    .split(/\s+/)
    .filter(Boolean).length;
  return Math.max(1, Math.ceil(wordCount / wordsPerMinute));
}

function formatBlogDate(dateString) {
  if (!dateString) return "N/A";
  const date = new Date(dateString);
  return date.toLocaleDateString("vi-VN", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
}

async function parseApiResponse(response) {
  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return response.json();
  }

  const text = await response.text();
  return { message: text };
}

function escapeHtml(value) {
  return String(value || "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/\"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

const sortedBlogs = computed(() => {
  const sorted = [...blogs.value];

  if (currentSort.value === "date-desc") {
    sorted.sort((a, b) => new Date(b.publishedAt) - new Date(a.publishedAt));
  } else if (currentSort.value === "date-asc") {
    sorted.sort((a, b) => new Date(a.publishedAt) - new Date(b.publishedAt));
  } else if (currentSort.value === "title-asc") {
    sorted.sort((a, b) => (a.title || "").localeCompare(b.title || ""));
  } else if (currentSort.value === "title-desc") {
    sorted.sort((a, b) => (b.title || "").localeCompare(a.title || ""));
  }

  return sorted;
});

const filteredBlogs = computed(() => {
  const q = searchQuery.value.trim().toLowerCase();
  if (!q) return sortedBlogs.value;
  return sortedBlogs.value.filter((post) =>
    String(post.title || "")
      .toLowerCase()
      .includes(q),
  );
});

const totalPages = computed(() =>
  Math.max(1, Math.ceil(filteredBlogs.value.length / pageSize)),
);

const paginatedBlogs = computed(() => {
  const start = currentPage.value * pageSize;
  return filteredBlogs.value.slice(start, start + pageSize);
});

const pageNumbers = computed(() =>
  Array.from({ length: totalPages.value }, (_, index) => index),
);

const resultsLabel = computed(
  () => `Tìm thấy ${filteredBlogs.value.length} bài viết`,
);

const detailContentHtml = computed(() => {
  const content =
    currentPost.value?.content || "Nội dung bài viết chưa được cập nhật.";
  return content
    .split("\n\n")
    .map((para) => `<p>${escapeHtml(para.trim())}</p>`)
    .join("");
});

const detailSummary = computed(
  () => currentPost.value?.summary || "Bài viết chưa có mô tả ngắn.",
);

const isCurrentPostFavorite = computed(() => {
  const id = currentPost.value?.id;
  return id != null ? favorites.value.includes(id) : false;
});

function setPage(page) {
  if (page < 0 || page > totalPages.value - 1) return;
  currentPage.value = page;
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function updateSearch(value) {
  searchQuery.value = value;
  currentPage.value = 0;
}

function updateSort(value) {
  currentSort.value = value;
  currentPage.value = 0;
}

function toggleViewMode(mode) {
  viewMode.value = mode;
}

function persistFavorites() {
  localStorage.setItem("blogFavorites", JSON.stringify(favorites.value));
}

function toggleBlogFavoriteQuick(postId) {
  const index = favorites.value.findIndex((id) => id === postId);
  if (index > -1) {
    favorites.value.splice(index, 1);
  } else {
    favorites.value.push(postId);
  }
  persistFavorites();
}

function toggleBlogFavorite() {
  if (!currentPost.value?.id) return;
  toggleBlogFavoriteQuick(currentPost.value.id);
}

async function shareBlogPost(platform) {
  if (platform !== "copy") return;
  try {
    await navigator.clipboard.writeText(window.location.href);
    window.alert("Đã sao chép liên kết vào clipboard!");
  } catch {
    window.alert("Không thể sao chép liên kết");
  }
}

function openBlogDetail(post) {
  currentPost.value = post;
  isDetailOpen.value = true;
}

function closeBlogDetail() {
  isDetailOpen.value = false;
  currentPost.value = null;
}

function doLogout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userEmail");
  router.push("/login");
}

async function fetchBlogs(forceReload = false) {
  if (loading.value) return;
  if (loaded.value && !forceReload) return;

  loading.value = true;
  error.value = "";
  currentPage.value = 0;

  try {
    const response = await fetch(
      `${API_BASE}/blogs?page=0&size=100&sort=publishedAt,desc`,
    );
    const result = await parseApiResponse(response);

    if (!response.ok) {
      throw new Error(result?.message || "Không tải được danh sách bài viết");
    }

    const pageData = result?.data;
    const list = Array.isArray(pageData?.content)
      ? pageData.content
      : Array.isArray(pageData)
        ? pageData
        : [];

    blogs.value = list;
    loaded.value = true;
  } catch (err) {
    error.value = err?.message || "Đã xảy ra lỗi khi tải bài viết";
  } finally {
    loading.value = false;
  }
}

watch([paginatedBlogs, viewMode, isDetailOpen, favorites], refreshIcons, {
  deep: true,
});

watch(totalPages, (value) => {
  if (currentPage.value > value - 1) {
    currentPage.value = Math.max(0, value - 1);
  }
});

onMounted(async () => {
  const token = localStorage.getItem("token");
  if (!token) {
    router.push("/login");
    return;
  }

  await fetchBlogs();
  refreshIcons();
});
</script>

<template>
  <div
    class="blog-page h-screen w-full overflow-hidden flex text-gray-700 bg-gray-50"
  >
    <aside
      class="hidden lg:flex flex-col w-64 bg-white border-r border-gray-200 z-30"
    >
      <div class="h-16 flex items-center px-6 border-b border-gray-100">
        <i data-lucide="zap" class="text-blue-600 mr-2 w-7 h-7"></i>
        <span
          class="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-600 to-cyan-500"
        >
          SmartHome
        </span>
      </div>

      <nav class="p-4 space-y-2 flex-1">
        <RouterLink
          to="/home"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="layout-dashboard" class="mr-3 w-5 h-5"></i>
          Tổng quan
        </RouterLink>
        <RouterLink
          to="/rooms"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="home" class="mr-3 w-5 h-5"></i>
          Phòng ốc
        </RouterLink>
        <RouterLink
          to="/home"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="activity" class="mr-3 w-5 h-5"></i>
          Thống kê điện
        </RouterLink>
        <RouterLink
          to="/home"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="user" class="mr-3 w-5 h-5"></i>
          Hồ sơ cá nhân
        </RouterLink>
        <RouterLink
          to="/blog"
          class="flex items-center px-4 py-3 text-blue-700 bg-blue-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="book-open" class="mr-3 w-5 h-5"></i>
          Blog
        </RouterLink>
      </nav>

      <div class="p-4 border-t border-gray-100">
        <button
          type="button"
          @click="doLogout"
          class="w-full flex items-center px-4 py-3 text-red-600 hover:bg-red-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="log-out" class="mr-3 w-5 h-5"></i>
          Đăng xuất
        </button>
      </div>
    </aside>

    <div class="flex-1 flex flex-col h-screen overflow-hidden">
      <header class="h-16 bg-white border-b border-gray-200 sticky top-0 z-40">
        <div
          class="w-full h-full px-4 sm:px-6 lg:px-8 flex items-center justify-between"
        >
          <div class="flex items-center gap-2">
            <i data-lucide="book-open" class="text-blue-600 w-6 h-6"></i>
            <h2 class="text-lg font-semibold text-gray-800">Blog</h2>
          </div>

          <nav class="flex items-center gap-4">
            <RouterLink
              to="/home"
              class="flex items-center gap-2 px-4 py-2 rounded-lg text-gray-600 hover:bg-gray-100 transition-colors font-medium"
              title="Quay về Dashboard chính"
            >
              <i data-lucide="arrow-left" class="w-4 h-4"></i>
              Dashboard
            </RouterLink>
            <button
              type="button"
              @click="doLogout"
              class="flex items-center gap-2 px-4 py-2 rounded-lg text-red-600 hover:bg-red-50 transition-colors font-medium"
            >
              <i data-lucide="log-out" class="w-4 h-4"></i>
              Đăng Xuất
            </button>
          </nav>
        </div>
      </header>

      <main
        class="flex-1 overflow-auto max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-12"
      >
        <section class="mb-12">
          <div class="flex items-center justify-between mb-4 gap-4 flex-wrap">
            <div>
              <h1 class="text-4xl font-bold text-gray-900 mb-2">
                Blog Tiết Kiệm Điện
              </h1>
              <p class="text-lg text-gray-600">
                Cập nhật các mẹo, kiến thức và kinh nghiệm sử dụng điện hiệu quả
              </p>
            </div>
            <button
              type="button"
              @click="fetchBlogs(true)"
              class="flex items-center gap-2 px-4 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700 transition-colors font-medium shadow-lg"
            >
              <i data-lucide="refresh-cw" class="w-5 h-5"></i>
              Làm mới
            </button>
          </div>
        </section>

        <section
          class="mb-8 bg-white p-6 rounded-2xl border border-gray-100 shadow-sm"
        >
          <div class="space-y-4">
            <div class="flex items-center gap-4 flex-wrap">
              <div class="relative flex-1 min-w-xs">
                <i
                  data-lucide="search"
                  class="absolute left-4 top-3 w-4 h-4 text-gray-400"
                ></i>
                <input
                  type="text"
                  placeholder="Tìm kiếm bài viết..."
                  class="w-full pl-10 pr-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  :value="searchQuery"
                  @input="updateSearch($event.target.value)"
                />
              </div>

              <select
                :value="currentSort"
                @change="updateSort($event.target.value)"
                class="px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-white focus:ring-2 focus:ring-blue-500 outline-none transition-all cursor-pointer"
              >
                <option value="date-desc">Mới nhất trước</option>
                <option value="date-asc">Cũ nhất trước</option>
                <option value="title-asc">A → Z</option>
                <option value="title-desc">Z → A</option>
              </select>
            </div>

            <div
              class="flex items-center justify-between text-sm gap-2 flex-wrap"
            >
              <span class="text-gray-600 font-medium">{{ resultsLabel }}</span>
              <div class="flex items-center gap-2">
                <button
                  type="button"
                  @click="toggleViewMode('grid')"
                  class="p-2 rounded-lg transition-colors"
                  :class="
                    viewMode === 'grid'
                      ? 'bg-blue-50 text-blue-600'
                      : 'hover:bg-gray-100 text-gray-600'
                  "
                >
                  <i data-lucide="grid" class="w-4 h-4"></i>
                </button>
                <button
                  type="button"
                  @click="toggleViewMode('list')"
                  class="p-2 rounded-lg transition-colors"
                  :class="
                    viewMode === 'list'
                      ? 'bg-blue-50 text-blue-600'
                      : 'hover:bg-gray-100 text-gray-600'
                  "
                >
                  <i data-lucide="list" class="w-4 h-4"></i>
                </button>
              </div>
            </div>
          </div>
        </section>

        <div class="mb-6">
          <div
            v-if="loading"
            class="p-4 rounded-xl bg-blue-50 text-blue-700 text-sm flex items-center gap-2"
          >
            <div class="animate-spin">
              <i data-lucide="loader-circle" class="w-4 h-4"></i>
            </div>
            Đang tải bài viết...
          </div>

          <div
            v-else-if="error"
            class="p-4 rounded-xl bg-red-50 border border-red-100 text-red-700 text-sm flex items-center justify-between gap-3"
          >
            <span>{{ error }}</span>
            <button
              type="button"
              @click="fetchBlogs(true)"
              class="px-3 py-1 rounded-lg bg-red-100 hover:bg-red-200 font-medium"
            >
              Thử lại
            </button>
          </div>

          <div
            v-else-if="!filteredBlogs.length"
            class="p-4 rounded-xl bg-gray-100 text-gray-600 text-sm"
          >
            {{
              searchQuery
                ? `Không tìm thấy bài viết với từ khóa: "${searchQuery}"`
                : "Chưa có bài viết nào."
            }}
          </div>
        </div>

        <div
          v-if="!loading && !error && paginatedBlogs.length"
          :class="
            viewMode === 'list'
              ? 'space-y-4'
              : 'grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6'
          "
        >
          <article
            v-for="post in paginatedBlogs"
            :key="post.id"
            class="bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm hover:shadow-lg transition-all cursor-pointer group"
            :class="viewMode === 'list' ? 'flex' : 'relative'"
            @click="openBlogDetail(post)"
          >
            <template v-if="viewMode === 'list'">
              <div class="w-48 h-32 overflow-hidden bg-gray-100 flex-shrink-0">
                <img
                  :src="post.thumbnailUrl || DEFAULT_THUMBNAIL"
                  :alt="post.title || 'Bài viết'"
                  class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                />
              </div>
              <div class="p-5 flex-1 flex flex-col">
                <h3
                  class="font-bold text-gray-800 text-lg mb-2 line-clamp-2 group-hover:text-blue-600 transition-colors"
                >
                  {{ post.title || "Bài viết chưa có tiêu đề" }}
                </h3>
                <p class="text-sm text-gray-500 mb-3 flex-1 line-clamp-2">
                  {{
                    post.summary ||
                    post.content ||
                    "Bài viết chưa có mô tả ngắn."
                  }}
                </p>
                <div
                  class="text-xs text-gray-400 flex items-center justify-between gap-2 flex-wrap"
                >
                  <span class="flex items-center"
                    ><i data-lucide="user" class="w-3 h-3 mr-1"></i
                    >{{ post.authorName || "SmartHome Team" }}</span
                  >
                  <span class="flex items-center"
                    ><i data-lucide="clock" class="w-3 h-3 mr-1"></i
                    >{{ calculateReadingTime(post.content) }} phút</span
                  >
                  <span class="flex items-center"
                    ><i data-lucide="calendar" class="w-3 h-3 mr-1"></i
                    >{{ formatBlogDate(post.publishedAt) }}</span
                  >
                  <button
                    type="button"
                    @click.stop="toggleBlogFavoriteQuick(post.id)"
                    class="ml-auto"
                  >
                    <i
                      data-lucide="heart"
                      class="w-4 h-4"
                      :class="
                        favorites.includes(post.id)
                          ? 'fill-red-500 text-red-500'
                          : 'text-gray-400'
                      "
                    ></i>
                  </button>
                </div>
              </div>
            </template>

            <template v-else>
              <button
                type="button"
                @click.stop="toggleBlogFavoriteQuick(post.id)"
                class="absolute top-3 right-3 z-10 p-2 bg-white rounded-full shadow-md hover:scale-110 transition-transform"
              >
                <i
                  data-lucide="heart"
                  class="w-5 h-5"
                  :class="
                    favorites.includes(post.id)
                      ? 'fill-red-500 text-red-500'
                      : 'text-gray-400'
                  "
                ></i>
              </button>
              <div class="h-44 overflow-hidden bg-gray-100 relative">
                <img
                  :src="post.thumbnailUrl || DEFAULT_THUMBNAIL"
                  :alt="post.title || 'Bài viết'"
                  class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                />
              </div>
              <div class="p-5">
                <h3
                  class="font-bold text-gray-800 text-lg mb-2 line-clamp-2 group-hover:text-blue-600 transition-colors"
                >
                  {{ post.title || "Bài viết chưa có tiêu đề" }}
                </h3>
                <p class="text-sm text-gray-500 mb-4 line-clamp-3">
                  {{
                    post.summary ||
                    post.content ||
                    "Bài viết chưa có mô tả ngắn."
                  }}
                </p>
                <div
                  class="text-xs text-gray-400 flex items-center justify-between gap-2 flex-wrap"
                >
                  <span class="flex items-center"
                    ><i data-lucide="user" class="w-3 h-3 mr-1"></i
                    >{{ post.authorName || "SmartHome Team" }}</span
                  >
                  <span class="flex items-center"
                    ><i data-lucide="clock" class="w-3 h-3 mr-1"></i
                    >{{ calculateReadingTime(post.content) }} phút</span
                  >
                  <span class="flex items-center"
                    ><i data-lucide="calendar" class="w-3 h-3 mr-1"></i
                    >{{ formatBlogDate(post.publishedAt) }}</span
                  >
                </div>
              </div>
            </template>
          </article>
        </div>

        <div
          v-if="!loading && !error && totalPages > 1"
          class="mt-12 flex items-center justify-center gap-3 flex-wrap"
        >
          <button
            v-if="currentPage > 0"
            type="button"
            @click="setPage(currentPage - 1)"
            class="px-4 py-2 rounded-lg border border-gray-200 hover:bg-gray-100 flex items-center gap-2 transition-colors"
          >
            <i data-lucide="chevron-left" class="w-4 h-4"></i>Trước
          </button>

          <button
            v-for="page in pageNumbers"
            :key="page"
            type="button"
            @click="setPage(page)"
            class="w-10 h-10 rounded-lg transition-colors font-medium"
            :class="
              page === currentPage
                ? 'bg-blue-600 text-white'
                : 'border border-gray-200 hover:bg-gray-100'
            "
          >
            {{ page + 1 }}
          </button>

          <button
            v-if="currentPage < totalPages - 1"
            type="button"
            @click="setPage(currentPage + 1)"
            class="px-4 py-2 rounded-lg border border-gray-200 hover:bg-gray-100 flex items-center gap-2 transition-colors"
          >
            Sau<i data-lucide="chevron-right" class="w-4 h-4"></i>
          </button>
        </div>
      </main>
    </div>

    <div
      v-if="isDetailOpen && currentPost"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4 py-6 overflow-y-auto"
      @click.self="closeBlogDetail"
    >
      <div
        class="bg-white w-full max-w-2xl rounded-3xl shadow-2xl overflow-hidden max-h-[90vh] overflow-y-auto my-6"
      >
        <div
          class="sticky top-0 bg-white border-b border-gray-100 p-6 flex justify-between items-start z-10"
        >
          <h2 class="text-2xl font-bold text-gray-800 pr-4 line-clamp-2">
            {{ currentPost.title || "Bài viết không có tiêu đề" }}
          </h2>
          <button
            type="button"
            @click="closeBlogDetail"
            class="text-gray-400 hover:text-gray-800 flex-shrink-0 p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <i data-lucide="x" class="w-6 h-6"></i>
          </button>
        </div>
        <div class="p-6 space-y-4">
          <img
            :src="currentPost.thumbnailUrl || DEFAULT_THUMBNAIL"
            :alt="currentPost.title || 'Blog thumbnail'"
            class="w-full h-64 object-cover rounded-2xl"
          />
          <div
            class="flex items-center justify-between text-sm text-gray-500 flex-wrap gap-2"
          >
            <span class="flex items-center"
              ><i data-lucide="user" class="w-4 h-4 mr-1"></i
              >{{ currentPost.authorName || "SmartHome Team" }}</span
            >
            <span class="flex items-center"
              ><i data-lucide="calendar" class="w-4 h-4 mr-1"></i
              >{{ formatBlogDate(currentPost.publishedAt) }}</span
            >
            <span class="flex items-center text-amber-600"
              ><i data-lucide="clock" class="w-4 h-4 mr-1"></i
              >{{ calculateReadingTime(currentPost.content) }} phút đọc</span
            >
          </div>
          <div
            class="p-4 bg-blue-50 rounded-xl text-blue-900 text-sm font-medium border border-blue-200"
          >
            {{ detailSummary }}
          </div>
          <div
            class="prose prose-sm text-gray-700 leading-relaxed space-y-3"
            v-html="detailContentHtml"
          ></div>

          <div
            class="mt-6 pt-4 border-t border-gray-100 flex items-center justify-between gap-2 flex-wrap"
          >
            <button
              type="button"
              @click="toggleBlogFavorite"
              class="flex items-center gap-2 px-4 py-2 rounded-lg border transition-colors"
              :class="
                isCurrentPostFavorite
                  ? 'bg-yellow-50 border-yellow-300 text-yellow-600'
                  : 'border-gray-200 hover:bg-yellow-50 hover:border-yellow-300 text-gray-600 hover:text-yellow-600'
              "
            >
              <i
                data-lucide="heart"
                class="w-4 h-4"
                :class="isCurrentPostFavorite ? 'fill-yellow-600' : ''"
              ></i>
              <span>Lưu bài</span>
            </button>
            <div class="flex items-center gap-2">
              <button
                type="button"
                title="Sao chép liên kết"
                @click="shareBlogPost('copy')"
                class="p-2 rounded-lg hover:bg-gray-100 text-gray-600 transition-colors hover:text-blue-600"
              >
                <i data-lucide="copy" class="w-4 h-4"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.blog-page {
  font-family: "Poppins", sans-serif;
}

::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.prose p {
  margin-bottom: 1rem;
  line-height: 1.8;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.min-w-xs {
  min-width: 20rem;
}

@media (max-width: 640px) {
  .min-w-xs {
    min-width: auto;
  }
}
</style>
