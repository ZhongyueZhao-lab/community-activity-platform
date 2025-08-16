<template>
  <div class="system-config">
    <!-- Page Background -->
    <div class="page-background">
      <div class="background-decoration"></div>
      <div class="background-pattern"></div>
      <div class="background-overlay"></div>
    </div>

    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="page-title">
            <span class="title-gradient">System Configuration</span>
          </h1>
          <p class="page-subtitle">Manage system settings and configurations</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="showAddConfigDialog" v-if="activeTab === 'all'" class="create-button">
            <el-icon><Plus /></el-icon>
            <span>Add Config</span>
          </el-button>
          <el-button type="primary" @click="showAddBannerDialog" v-if="activeTab === 'banner'" class="create-button">
            <el-icon><Plus /></el-icon>
            <span>Add Banner</span>
          </el-button>
          <el-button type="primary" @click="showAddNoticeDialog" v-if="activeTab === 'notice'" class="create-button">
            <el-icon><Plus /></el-icon>
            <span>Add Notice</span>
          </el-button>
          <el-button type="primary" @click="showAddCategoryDialog" v-if="activeTab === 'category'" class="create-button">
            <el-icon><Plus /></el-icon>
            <span>Add Category</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- Configuration Tabs -->
    <div class="config-tabs-section">
      <div class="tabs-background"></div>
      <div class="tabs-content">
        <el-tabs v-model="activeTab" type="card" class="modern-tabs">
          <!-- System Configuration -->
          <el-tab-pane label="System Config" name="system">
            <div class="config-section">
              <el-row :gutter="24">
                <el-col :lg="12" :md="24" :sm="24">
                  <div class="config-card">
                    <div class="card-header">
                      <h3 class="card-title">
                        <el-icon class="card-icon"><Setting /></el-icon>
                        Basic Settings
                      </h3>
                    </div>
                    <div class="card-content">
                      <el-form :model="systemConfigs" label-width="120px" class="modern-form">
                        <el-form-item label="Site Name">
                          <el-input
                              v-model="systemConfigs.site_name"
                              @blur="updateConfig('site_name', systemConfigs.site_name)"
                              class="modern-input"
                              placeholder="Enter site name..."
                          />
                        </el-form-item>
                        <el-form-item label="Site Description">
                          <el-input
                              v-model="systemConfigs.site_description"
                              type="textarea"
                              :rows="3"
                              @blur="updateConfig('site_description', systemConfigs.site_description)"
                              class="modern-textarea"
                              placeholder="Enter site description..."
                          />
                        </el-form-item>
                        <el-form-item label="Contact Email">
                          <el-input
                              v-model="systemConfigs.contact_email"
                              @blur="updateConfig('contact_email', systemConfigs.contact_email)"
                              class="modern-input"
                              placeholder="Enter contact email..."
                          />
                        </el-form-item>
                        <el-form-item label="Contact Phone">
                          <el-input
                              v-model="systemConfigs.contact_phone"
                              @blur="updateConfig('contact_phone', systemConfigs.contact_phone)"
                              class="modern-input"
                              placeholder="Enter contact phone..."
                          />
                        </el-form-item>
                      </el-form>
                    </div>
                  </div>
                </el-col>
                <el-col :lg="12" :md="24" :sm="24">
                  <div class="config-card">
                    <div class="card-header">
                      <h3 class="card-title">
                        <el-icon class="card-icon"><Tools /></el-icon>
                        Feature Settings
                      </h3>
                    </div>
                    <div class="card-content">
                      <el-form :model="systemConfigs" label-width="120px" class="modern-form">
                        <el-form-item label="Open Registration">
                          <el-switch
                              v-model="systemConfigs.registration_enabled"
                              :active-value="'1'"
                              :inactive-value="'0'"
                              @change="updateConfig('registration_enabled', systemConfigs.registration_enabled)"
                              class="modern-switch"
                          />
                        </el-form-item>
                        <el-form-item label="Auto Publish Events">
                          <el-switch
                              v-model="systemConfigs.activity_auto_publish"
                              :active-value="'1'"
                              :inactive-value="'0'"
                              @change="updateConfig('activity_auto_publish', systemConfigs.activity_auto_publish)"
                              class="modern-switch"
                          />
                        </el-form-item>
                        <el-form-item label="Enable SMS">
                          <el-switch
                              v-model="systemConfigs.sms_enabled"
                              :active-value="'1'"
                              :inactive-value="'0'"
                              @change="updateConfig('sms_enabled', systemConfigs.sms_enabled)"
                              class="modern-switch"
                          />
                        </el-form-item>
                        <el-form-item label="Enable Email">
                          <el-switch
                              v-model="systemConfigs.email_enabled"
                              :active-value="'1'"
                              :inactive-value="'0'"
                              @change="updateConfig('email_enabled', systemConfigs.email_enabled)"
                              class="modern-switch"
                          />
                        </el-form-item>
                        <el-form-item label="Max Upload Size">
                          <div class="upload-size-input">
                            <el-input-number
                                v-model="systemConfigs.max_upload_size"
                                :min="1048576"
                                :max="104857600"
                                :step="1048576"
                                @blur="updateConfig('max_upload_size', systemConfigs.max_upload_size.toString())"
                                class="modern-number-input"
                            />
                            <span class="unit-text">MB</span>
                          </div>
                        </el-form-item>
                      </el-form>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-tab-pane>

          <!-- Banner Management -->
          <el-tab-pane label="Banner Management" name="banner">
            <div class="table-section">
              <div class="table-header">
                <h3 class="results-title">Banner List</h3>
                <p class="results-subtitle">{{ banners.length }} banners found</p>
              </div>

              <div class="table-container">
                <el-table
                    :data="banners"
                    v-loading="bannerLoading"
                    class="modern-table"
                    :border="false"
                    :stripe="true"
                    empty-text="No banners found"
                >
                  <el-table-column prop="id" label="ID" width="70" />

                  <el-table-column prop="title" label="Title" width="180" show-overflow-tooltip>
                    <template #default="scope">
                      <div class="title-cell">
                        <div class="title-text">{{ scope.row.title }}</div>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column label="Image" width="100">
                    <template #default="scope">
                      <div class="image-preview">
                        <el-image
                            v-if="scope.row.imageUrl"
                            :src="scope.row.imageUrl"
                            style="width: 50px; height: 30px;"
                            fit="cover"
                            :preview-src-list="[scope.row.imageUrl]"
                            class="banner-image"
                        />
                        <div v-else class="no-image">
                          <el-icon><Picture /></el-icon>
                        </div>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column prop="linkUrl" label="Link" width="100" show-overflow-tooltip class-name="hidden-sm">
                    <template #default="scope">
                      <div class="link-info">
                        <el-icon class="link-icon"><Link /></el-icon>
                        <span>{{ scope.row.linkUrl || '-' }}</span>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column prop="sortOrder" label="Sort" width="80" class-name="hidden-xs" />

                  <el-table-column label="Status" width="100">
                    <template #default="scope">
                      <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" class="modern-status-tag">
                        <div class="status-dot"></div>
                        {{ scope.row.status === 1 ? 'Active' : 'Disabled' }}
                      </el-tag>
                    </template>
                  </el-table-column>

                  <el-table-column label="Created" width="170" class-name="hidden-md">
                    <template #default="scope">
                      <div class="date-info">
                        <el-icon class="date-icon"><Clock /></el-icon>
                        <span>{{ formatDateTime(scope.row.createdAt) }}</span>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column label="Actions" width="120" fixed="right">
                    <template #default="scope">
                      <div class="table-actions">
                        <el-button size="small" type="primary" @click="editBanner(scope.row)" class="action-btn">
                          <el-icon><Edit /></el-icon>
                        </el-button>
                        <el-button size="small" type="danger" @click="deleteBanner(scope.row)" class="action-btn">
                          <el-icon><Delete /></el-icon>
                        </el-button>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-tab-pane>

          <!-- Notice Management -->
          <el-tab-pane label="Announcements" name="notice">
            <div class="table-section">
              <div class="table-header">
                <h3 class="results-title">Announcement List</h3>
                <p class="results-subtitle">{{ noticePagination.total }} announcements found</p>
              </div>

              <div class="table-container">
                <el-table
                    :data="notices"
                    v-loading="noticeLoading"
                    class="modern-table"
                    :border="false"
                    :stripe="true"
                    empty-text="No announcements found"
                >
                  <el-table-column prop="id" label="ID" width="70" />

                  <el-table-column prop="title" label="Title" width="100" show-overflow-tooltip>
                    <template #default="scope">
                      <div class="title-cell">
                        <div class="title-text">{{ scope.row.title }}</div>
                        <div class="title-type">
                          <el-tag :type="getNoticeTypeColor(scope.row.type)" size="small" class="mini-tag">
                            {{ getNoticeTypeText(scope.row.type) }}
                          </el-tag>
                        </div>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column label="Content" width="250" show-overflow-tooltip class-name="hidden-sm">
                    <template #default="scope">
                      <div class="content-preview">
                        <el-icon class="content-icon"><Document /></el-icon>
                        <span>{{ scope.row.content.substring(0, 30) }}{{ scope.row.content.length > 30 ? '...' : '' }}</span>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column label="Target" width="90" class-name="hidden-md">
                    <template #default="scope">
                      <el-tag size="small" class="mini-tag">
                        {{ getTargetTypeText(scope.row.targetType) }}
                      </el-tag>
                    </template>
                  </el-table-column>

                  <el-table-column label="Status" width="100">
                    <template #default="scope">
                      <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" class="modern-status-tag">
                        <div class="status-dot"></div>
                        {{ scope.row.status === 1 ? 'Active' : 'Disabled' }}
                      </el-tag>
                    </template>
                  </el-table-column>

                  <el-table-column label="Published" width="120" class-name="hidden-xs">
                    <template #default="scope">
                      <div class="date-info">
                        <el-icon class="date-icon"><Clock /></el-icon>
                        <span>{{ formatDateTime(scope.row.createdAt) }}</span>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column label="Actions" width="180" fixed="right">
                    <template #default="scope">
                      <div class="table-actions">
                        <el-button size="small" type="primary" @click="viewNotice(scope.row)" class="action-btn">
                          <el-icon><View /></el-icon>
                        </el-button>
                        <el-dropdown @command="(command) => handleNoticeCommand(command, scope.row)" size="small">
                          <el-button size="small" class="action-btn more-btn">
                            <el-icon><MoreFilled /></el-icon>
                          </el-button>
                          <template #dropdown>
                            <el-dropdown-menu class="modern-dropdown">
                              <el-dropdown-item command="edit">
                                <el-icon><Edit /></el-icon>
                                Edit
                              </el-dropdown-item>
                              <el-dropdown-item :command="scope.row.status === 1 ? 'disable' : 'enable'">
                                <el-icon>
                                  <CircleClose v-if="scope.row.status === 1" />
                                  <CircleCheck v-else />
                                </el-icon>
                                {{ scope.row.status === 1 ? 'Disable' : 'Enable' }}
                              </el-dropdown-item>
                              <el-dropdown-item command="delete" divided class="danger-item">
                                <el-icon><Delete /></el-icon>
                                Delete
                              </el-dropdown-item>
                            </el-dropdown-menu>
                          </template>
                        </el-dropdown>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </div>

              <!-- Pagination -->
              <div class="pagination-section" v-if="noticePagination.total > 0">
                <div class="pagination-wrapper">
                  <el-config-provider :locale="enLocale">
                    <el-pagination
                        v-model:current-page="noticePagination.page"
                        v-model:page-size="noticePagination.size"
                        :page-sizes="[10, 20, 50, 100]"
                        :total="noticePagination.total"
                        :layout="paginationLayout"
                        @size-change="handleNoticePageSizeChange"
                        @current-change="handleNoticePageChange"
                        class="modern-pagination"
                    />
                  </el-config-provider>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- Category Management -->
          <el-tab-pane label="Categories" name="category">
            <div class="table-section">
              <div class="table-header">
                <h3 class="results-title">Category List</h3>
                <p class="results-subtitle">{{ categories.length }} categories found</p>
              </div>

              <div class="table-container">
                <el-table
                    :data="categories"
                    v-loading="categoryLoading"
                    class="modern-table"
                    :border="false"
                    :stripe="true"
                    empty-text="No categories found"
                >
                  <el-table-column prop="id" label="ID" width="70" />

                  <el-table-column prop="name" label="Name" width="120" show-overflow-tooltip>
                    <template #default="scope">
                      <div class="category-name">
                        <el-icon class="category-icon"><Folder /></el-icon>
                        <span>{{ scope.row.name }}</span>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column prop="description" label="Description" width="180" show-overflow-tooltip class-name="hidden-sm" />

                  <el-table-column prop="sortOrder" label="Sort" width="80" class-name="hidden-xs" />

                  <el-table-column label="Status" width="100">
                    <template #default="scope">
                      <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" class="modern-status-tag">
                        <div class="status-dot"></div>
                        {{ scope.row.status === 1 ? 'Active' : 'Disabled' }}
                      </el-tag>
                    </template>
                  </el-table-column>

                  <el-table-column label="Created" width="160" class-name="hidden-md">
                    <template #default="scope">
                      <div class="date-info">
                        <el-icon class="date-icon"><Clock /></el-icon>
                        <span>{{ formatDateTime(scope.row.createdAt) }}</span>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column label="Actions" width="230" fixed="right">
                    <template #default="scope">
                      <div class="table-actions">
                        <el-button size="small" type="primary" @click="editCategory(scope.row)" class="action-btn">
                          <el-icon><Edit /></el-icon>
                        </el-button>
                        <el-button size="small" type="danger" @click="deleteCategory(scope.row)" class="action-btn">
                          <el-icon><Delete /></el-icon>
                        </el-button>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-tab-pane>

          <!-- All Configurations -->
          <el-tab-pane label="All Configs" name="all">
            <div class="table-section">
              <div class="table-header">
                <h3 class="results-title">Configuration List</h3>
                <p class="results-subtitle">{{ allConfigs.length }} configurations found</p>
              </div>

              <div class="table-container">
                <el-table
                    :data="allConfigs"
                    v-loading="configLoading"
                    class="modern-table"
                    :border="false"
                    :stripe="true"
                    empty-text="No configurations found"
                >
                  <el-table-column prop="id" label="ID" width="70" />

                  <el-table-column prop="configKey" label="Config Key" width="140" show-overflow-tooltip>
                    <template #default="scope">
                      <div class="config-key">
                        <el-icon class="key-icon"><Key /></el-icon>
                        <span>{{ scope.row.configKey }}</span>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column prop="configValue" label="Value" width="100" show-overflow-tooltip>
                    <template #default="scope">
                      <div class="config-value">
                        <el-tag v-if="scope.row.configType === 'boolean'" :type="scope.row.configValue === '1' ? 'success' : 'danger'" size="small">
                          {{ scope.row.configValue === '1' ? 'true' : 'false' }}
                        </el-tag>
                        <span v-else>{{ scope.row.configValue }}</span>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column prop="configType" label="Type" width="90" class-name="hidden-sm">
                    <template #default="scope">
                      <el-tag type="info" size="small" class="type-tag">{{ scope.row.configType }}</el-tag>
                    </template>
                  </el-table-column>

                  <el-table-column prop="description" label="Description" width="120" show-overflow-tooltip class-name="hidden-xs" />

                  <el-table-column label="Editable" width="160" class-name="hidden-md">
                    <template #default="scope">
                      <el-tag :type="scope.row.isEditable === 1 ? 'success' : 'info'" size="small">
                        {{ scope.row.isEditable === 1 ? 'Yes' : 'No' }}
                      </el-tag>
                    </template>
                  </el-table-column>

                  <el-table-column label="Updated" width="160" class-name="hidden-lg">
                    <template #default="scope">
                      <div class="date-info">
                        <el-icon class="date-icon"><Clock /></el-icon>
                        <span>{{ formatDateTime(scope.row.updatedAt) }}</span>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column label="Actions" width="120" fixed="right">
                    <template #default="scope">
                      <div class="table-actions">
                        <el-button
                            size="small"
                            type="primary"
                            @click="editConfig(scope.row)"
                            :disabled="scope.row.isEditable === 0"
                            class="action-btn"
                        >
                          <el-icon><Edit /></el-icon>
                        </el-button>
                        <el-button
                            size="small"
                            type="danger"
                            @click="deleteConfig(scope.row)"
                            :disabled="scope.row.isEditable === 0"
                            class="action-btn"
                        >
                          <el-icon><Delete /></el-icon>
                        </el-button>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>


    <!-- Banner Edit Dialog -->
    <el-dialog
        v-model="bannerDialogVisible"
        :title="bannerDialogTitle"
        width="70%"
        :close-on-click-modal="false"
        :lock-scroll="false"
        :modal-append-to-body="false"
        :append-to-body="true"
        class="modern-dialog"
        :before-close="handleDialogClose"
        @open="handleDialogOpen"
        @opened="handleDialogOpened"
        @close="handleDialogClosed"
    >
      <div class="dialog-content">
        <el-form
            ref="bannerFormRef"
            :model="currentBanner"
            :rules="bannerRules"
            label-width="100px"
            class="modern-form"
        >
          <el-form-item label="Title" prop="title">
            <el-input v-model="currentBanner.title" placeholder="Enter banner title..." class="modern-input" />
          </el-form-item>
          <el-form-item label="Image" prop="imageUrl">
            <div class="image-upload-wrapper">
              <ImageUpload
                  v-model="currentBanner.imageUrl"
                  :limit="1"
                  :max-size="10 * 1024 * 1024"
                  accept=".jpg,.jpeg,.png,.gif,.bmp,.webp"
                  tips="Recommended size: 1200x400 pixels, supports jpg, png, gif, bmp, webp formats, max 10MB"
                  @success="handleImageUploadSuccess"
                  @error="handleImageUploadError"
                  @remove="handleImageRemove"
              />
              <div class="upload-help-text">
                <p>* Admins can upload images to server</p>
                <p>* Images will be automatically set as banner after successful upload</p>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="Link URL" prop="linkUrl">
            <el-input v-model="currentBanner.linkUrl" placeholder="Enter link URL (optional)..." class="modern-input" />
          </el-form-item>
          <el-form-item label="Sort Order" prop="sortOrder">
            <el-input-number
                v-model="currentBanner.sortOrder"
                :min="0"
                :max="999"
                placeholder="Enter sort order..."
                style="width: 100%"
                class="modern-number-input"
            />
          </el-form-item>
          <el-form-item label="Status" prop="status">
            <el-radio-group v-model="currentBanner.status" class="modern-radio-group">
              <el-radio :label="1">Active</el-radio>
              <el-radio :label="0">Disabled</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="bannerDialogVisible = false" class="cancel-button">Cancel</el-button>
          <el-button
              type="primary"
              @click="saveBanner"
              :loading="bannerSaving"
              :disabled="!currentBanner.imageUrl"
              class="save-button"
          >
            {{ isEditBanner ? 'Update' : 'Create' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Category Edit Dialog -->
    <el-dialog
        v-model="categoryDialogVisible"
        :title="categoryDialogTitle"
        width="60%"
        :close-on-click-modal="false"
        :lock-scroll="false"
        :modal-append-to-body="false"
        :append-to-body="true"
        class="modern-dialog"
        :before-close="handleDialogClose"
        @open="handleDialogOpen"
        @opened="handleDialogOpened"
        @close="handleDialogClosed"
    >
      <div class="dialog-content">
        <el-form
            ref="categoryFormRef"
            :model="currentCategory"
            :rules="categoryRules"
            label-width="120px"
            class="modern-form"
        >
          <el-form-item label="Category Name" prop="name">
            <el-input v-model="currentCategory.name" placeholder="Enter category name..." class="modern-input" />
          </el-form-item>
          <el-form-item label="Description" prop="description">
            <el-input
                v-model="currentCategory.description"
                type="textarea"
                :rows="3"
                placeholder="Enter category description..."
                class="modern-textarea"
            />
          </el-form-item>
          <el-form-item label="Sort Order" prop="sortOrder">
            <el-input-number
                v-model="currentCategory.sortOrder"
                :min="0"
                :max="999"
                placeholder="Enter sort order..."
                style="width: 100%"
                class="modern-number-input"
            />
          </el-form-item>
          <el-form-item label="Status" prop="status">
            <el-radio-group v-model="currentCategory.status" class="modern-radio-group">
              <el-radio :label="1">Active</el-radio>
              <el-radio :label="0">Disabled</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="categoryDialogVisible = false" class="cancel-button">Cancel</el-button>
          <el-button
              type="primary"
              @click="saveCategory"
              :loading="categorySaving"
              class="save-button"
          >
            {{ isEditCategory ? 'Update' : 'Create' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Config Edit Dialog -->
    <el-dialog
        v-model="configDialogVisible"
        :title="configDialogTitle"
        width="60%"
        :close-on-click-modal="false"
        :lock-scroll="false"
        :modal-append-to-body="false"
        :append-to-body="true"
        class="modern-dialog"
        :before-close="handleDialogClose"
        @open="handleDialogOpen"
        @opened="handleDialogOpened"
        @close="handleDialogClosed"
    >
      <div class="dialog-content">
        <el-form
            ref="configFormRef"
            :model="currentConfig"
            :rules="configRules"
            label-width="120px"
            class="modern-form"
        >
          <el-form-item label="Config Key" prop="configKey">
            <el-input
                v-model="currentConfig.configKey"
                placeholder="Enter config key..."
                :disabled="isEditConfig"
                class="modern-input"
            />
          </el-form-item>
          <el-form-item label="Config Value" prop="configValue">
            <el-input
                v-if="currentConfig.configType === 'string'"
                v-model="currentConfig.configValue"
                placeholder="Enter config value..."
                class="modern-input"
            />
            <el-input
                v-else-if="currentConfig.configType === 'number'"
                v-model="currentConfig.configValue"
                type="number"
                placeholder="Enter number..."
                class="modern-input"
            />
            <el-switch
                v-else-if="currentConfig.configType === 'boolean'"
                v-model="currentConfig.configValue"
                active-value="1"
                inactive-value="0"
                class="modern-switch"
            />
            <el-input
                v-else
                v-model="currentConfig.configValue"
                type="textarea"
                :rows="4"
                placeholder="Enter JSON format config value..."
                class="modern-textarea"
            />
          </el-form-item>
          <el-form-item label="Config Type" prop="configType">
            <el-select
                v-model="currentConfig.configType"
                placeholder="Select config type..."
                :disabled="isEditConfig"
                style="width: 100%"
                class="modern-select"
            >
              <el-option label="String" value="STRING" />
              <el-option label="Number" value="NUMBER" />
              <el-option label="Boolean" value="BOOLEAN" />
              <el-option label="JSON" value="JSON" />
            </el-select>
          </el-form-item>
          <el-form-item label="Description" prop="description">
            <el-input
                v-model="currentConfig.description"
                type="textarea"
                :rows="3"
                placeholder="Enter config description..."
                class="modern-textarea"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="configDialogVisible = false" class="cancel-button">Cancel</el-button>
          <el-button
              type="primary"
              @click="saveConfig"
              :loading="configSaving"
              class="save-button"
          >
            {{ isEditConfig ? 'Update' : 'Create' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Notice Edit Dialog -->
    <el-dialog
        v-model="noticeDialogVisible"
        :title="noticeDialogTitle"
        width="70%"
        :close-on-click-modal="false"
        :lock-scroll="false"
        :modal-append-to-body="false"
        :append-to-body="true"
        class="modern-dialog"
        :before-close="handleDialogClose"
        @open="handleDialogOpen"
        @opened="handleDialogOpened"
        @close="handleDialogClosed"
    >
      <div class="dialog-content">
        <el-form
            ref="noticeFormRef"
            :model="currentNotice"
            :rules="noticeRules"
            label-width="120px"
            class="modern-form"
        >
          <el-form-item label="Title" prop="title">
            <el-input v-model="currentNotice.title" placeholder="Enter announcement title..." class="modern-input" />
          </el-form-item>
          <el-form-item label="Content" prop="content">
            <el-input
                v-model="currentNotice.content"
                type="textarea"
                :rows="6"
                placeholder="Enter announcement content..."
                class="modern-textarea"
            />
          </el-form-item>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="Type" prop="type">
                <el-select v-model="currentNotice.type" placeholder="Select type..." class="modern-select" style="width: 100%">
                  <el-option label="System Message" value="system" />
                  <el-option label="Activity Message" value="activity" />
                  <el-option label="General Notice" value="notice" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Target Users" prop="targetType">
                <el-select v-model="currentNotice.targetType" placeholder="Select target users..." class="modern-select" style="width: 100%">
                  <el-option label="All Users" value="all" />
                  <el-option label="Regular Users" value="user" />
                  <el-option label="Administrators" value="admin" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="Status" prop="status">
            <el-radio-group v-model="currentNotice.status" class="modern-radio-group">
              <el-radio :label="1">Active</el-radio>
              <el-radio :label="0">Disabled</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="noticeDialogVisible = false" class="cancel-button">Cancel</el-button>
          <el-button
              type="primary"
              @click="saveNotice"
              :loading="noticeSaving"
              class="save-button"
          >
            {{ isEditNotice ? 'Update' : 'Publish' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Notice View Dialog -->
    <el-dialog
        v-model="noticeViewDialogVisible"
        title="View Announcement"
        width="70%"
        :close-on-click-modal="false"
        :lock-scroll="false"
        :modal-append-to-body="false"
        :append-to-body="true"
        class="modern-dialog"
        :before-close="handleDialogClose"
        @open="handleDialogOpen"
        @opened="handleDialogOpened"
        @close="handleDialogClosed"
    >
      <div v-if="viewNoticeData" class="dialog-content">
        <div class="notice-detail">
          <el-descriptions :column="2" border size="large" class="modern-descriptions">
            <el-descriptions-item label="Title" :span="2">{{ viewNoticeData.title }}</el-descriptions-item>
            <el-descriptions-item label="Type">
              <el-tag :type="getNoticeTypeColor(viewNoticeData.type)" class="detail-tag">
                {{ getNoticeTypeText(viewNoticeData.type) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Target Users">
              <el-tag class="detail-tag">{{ getTargetTypeText(viewNoticeData.targetType) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Status">
              <el-tag :type="viewNoticeData.status === 1 ? 'success' : 'danger'" class="detail-tag">
                {{ viewNoticeData.status === 1 ? 'Active' : 'Disabled' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Published Time">
              {{ formatDateTime(viewNoticeData.createdAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="Content" :span="2">
              <div class="notice-content">{{ viewNoticeData.content }}</div>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="noticeViewDialogVisible = false" class="cancel-button">Close</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox, ElConfigProvider } from 'element-plus'
import {
  Plus, Setting, Tools, Picture, Link, Clock, Edit, Delete, Document, View, MoreFilled,
  CircleClose, CircleCheck, Folder, Key
} from '@element-plus/icons-vue'
import en from 'element-plus/dist/locale/en.mjs'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import dayjs from 'dayjs'
import ImageUpload from '@/components/ImageUpload.vue'

// English locale configuration
const enLocale = en

const userStore = useUserStore()

// Responsive data
const activeTab = ref('system')
const configLoading = ref(false)
const categoryLoading = ref(false)
const bannerLoading = ref(false)
const noticeLoading = ref(false)

// Dialog states
const categoryDialogVisible = ref(false)
const configDialogVisible = ref(false)
const bannerDialogVisible = ref(false)
const noticeDialogVisible = ref(false)
const noticeViewDialogVisible = ref(false)

const categoryDialogTitle = ref('')
const configDialogTitle = ref('')
const bannerDialogTitle = ref('')
const noticeDialogTitle = ref('')

const categorySaving = ref(false)
const configSaving = ref(false)
const bannerSaving = ref(false)
const noticeSaving = ref(false)

const isEditCategory = ref(false)
const isEditConfig = ref(false)
const isEditBanner = ref(false)
const isEditNotice = ref(false)

// Store original body overflow for dialogs
let originalBodyOverflow = ''
let originalHtmlOverflow = ''

// Computed property for responsive pagination layout
const paginationLayout = computed(() => {
  if (window.innerWidth < 768) {
    return 'prev, pager, next'
  } else if (window.innerWidth < 1024) {
    return 'total, prev, pager, next'
  } else {
    return 'total, sizes, prev, pager, next, jumper'
  }
})

// Data lists
const systemConfigs = reactive({
  site_name: '',
  site_description: '',
  contact_email: '',
  contact_phone: '',
  registration_enabled: '1',
  activity_auto_publish: '0',
  sms_enabled: '0',
  email_enabled: '1',
  max_upload_size: 10485760
})

const categories = ref([])
const allConfigs = ref([])
const banners = ref([])
const notices = ref([])
const viewNoticeData = ref(null)

// Pagination data
const noticePagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// Form data
const currentCategory = ref({
  id: null,
  name: '',
  description: '',
  sortOrder: 0,
  status: 1
})

const currentConfig = ref({
  id: null,
  configKey: '',
  configValue: '',
  configType: 'string',
  description: ''
})

const currentBanner = ref({
  id: null,
  title: '',
  imageUrl: '',
  linkUrl: '',
  sortOrder: 0,
  status: 1
})

const currentNotice = ref({
  id: null,
  title: '',
  content: '',
  type: 'notice',
  targetType: 'all',
  status: 1
})

// Form references
const categoryFormRef = ref()
const configFormRef = ref()
const bannerFormRef = ref()
const noticeFormRef = ref()

// Form validation rules
const categoryRules = {
  name: [{ required: true, message: 'Please enter category name', trigger: 'blur' }],
  sortOrder: [{ required: true, message: 'Please enter sort order', trigger: 'blur' }]
}

const configRules = {
  configKey: [{ required: true, message: 'Please enter config key', trigger: 'blur' }],
  configValue: [{ required: true, message: 'Please enter config value', trigger: 'blur' }],
  configType: [{ required: true, message: 'Please select config type', trigger: 'change' }]
}

const bannerRules = {
  title: [{ required: true, message: 'Please enter banner title', trigger: 'blur' }],
  imageUrl: [{ required: true, message: 'Please upload banner image', trigger: 'blur' }],
  sortOrder: [{ required: true, message: 'Please enter sort order', trigger: 'blur' }]
}

const noticeRules = {
  title: [{ required: true, message: 'Please enter announcement title', trigger: 'blur' }],
  content: [{ required: true, message: 'Please enter announcement content', trigger: 'blur' }],
  type: [{ required: true, message: 'Please select announcement type', trigger: 'change' }],
  targetType: [{ required: true, message: 'Please select target users', trigger: 'change' }]
}

// Methods
const loadSystemConfigs = async () => {
  try {
    const response = await request.get('/api/admin/system/configs')
    if (response.code === 200) {
      const configs = response.data
      configs.forEach(config => {
        if (systemConfigs.hasOwnProperty(config.configKey)) {
          if (config.configType === 'number') {
            systemConfigs[config.configKey] = parseInt(config.configValue)
          } else {
            systemConfigs[config.configKey] = config.configValue
          }
        }
      })
    }
  } catch (error) {
    console.error('Failed to fetch system configs:', error)
    ElMessage.error('Failed to fetch system configs')
  }
}

const loadCategories = async () => {
  try {
    categoryLoading.value = true
    const response = await request.get('/api/admin/categories')
    if (response.code === 200) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('Failed to fetch categories:', error)
    ElMessage.error('Failed to fetch categories')
  } finally {
    categoryLoading.value = false
  }
}

const loadAllConfigs = async () => {
  try {
    configLoading.value = true
    const response = await request.get('/api/admin/system/configs')
    if (response.code === 200) {
      allConfigs.value = response.data
    }
  } catch (error) {
    console.error('Failed to fetch configs:', error)
    ElMessage.error('Failed to fetch configs')
  } finally {
    configLoading.value = false
  }
}

const loadBanners = async () => {
  try {
    bannerLoading.value = true
    const response = await request.get('/api/admin/banners')
    if (response.code === 200) {
      banners.value = response.data.records || response.data || []
    }
  } catch (error) {
    console.error('Failed to fetch banners:', error)
    ElMessage.error('Failed to fetch banners')
    banners.value = []
  } finally {
    bannerLoading.value = false
  }
}

const loadNotices = async () => {
  try {
    noticeLoading.value = true
    const params = {
      page: noticePagination.page,
      size: noticePagination.size
    }
    const response = await request.get('/api/admin/messages', { params })
    if (response.code === 200) {
      notices.value = response.data.records || []
      noticePagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('Failed to fetch announcements:', error)
    ElMessage.error('Failed to fetch announcements')
    notices.value = []
  } finally {
    noticeLoading.value = false
  }
}

const updateConfig = async (key, value) => {
  try {

    let configType = 'STRING'

    if (typeof value === 'number') {
      configType = 'NUMBER'
    } else if (value === '1' || value === '0' || value === true || value === false) {
      configType = 'BOOLEAN'
    } else if (typeof value === 'string') {
      // 尝试解析JSON
      try {
        JSON.parse(value)
        configType = 'JSON'
      } catch (e) {
        configType = 'STRING'
      }
    }

    const config = {
      configKey: key,
      configValue: value.toString(),
      configType: configType,
      description: getConfigDescription(key)
    }

    const response = await request.post('/api/admin/system/configs', config)
    if (response.code === 200) {
      ElMessage.success('Configuration updated successfully')
      loadAllConfigs()
    }
  } catch (error) {
    console.error('Failed to update config:', error)
    ElMessage.error('Failed to update config')
  }
}

const getConfigDescription = (key) => {
  const descriptions = {
    site_name: 'Website name',
    site_description: 'Website description',
    contact_email: 'Contact email',
    contact_phone: 'Contact phone',
    registration_enabled: 'Enable user registration',
    activity_auto_publish: 'Auto publish events (skip review)',
    sms_enabled: 'Enable SMS functionality',
    email_enabled: 'Enable email functionality',
    max_upload_size: 'Maximum file upload size (bytes)'
  }
  return descriptions[key] || ''
}

// Dialog scroll control methods
const handleDialogOpen = () => {
  originalBodyOverflow = document.body.style.overflow || ''
  originalHtmlOverflow = document.documentElement.style.overflow || ''

  nextTick(() => {
    document.body.style.overflow = 'auto'
    document.documentElement.style.overflow = 'auto'
  })
}

const handleDialogOpened = () => {
  nextTick(() => {
    document.body.style.overflow = 'auto'
    document.documentElement.style.overflow = 'auto'
    document.body.classList.remove('el-popup-parent--hidden')
  })
}

const handleDialogClose = (done) => {
  done()
}

const handleDialogClosed = () => {
  nextTick(() => {
    document.body.style.overflow = originalBodyOverflow
    document.documentElement.style.overflow = originalHtmlOverflow
    document.body.classList.remove('el-popup-parent--hidden')
  })
}

// Banner management methods
const showAddBannerDialog = () => {
  if (!userStore.isAdmin) {
    ElMessage.error('Insufficient permissions, only admins can add banners')
    return
  }

  isEditBanner.value = false
  bannerDialogTitle.value = 'Add Banner'
  currentBanner.value = {
    id: null,
    title: '',
    imageUrl: '',
    linkUrl: '',
    sortOrder: 0,
    status: 1
  }
  bannerDialogVisible.value = true
}

const editBanner = (banner) => {
  if (!userStore.isAdmin) {
    ElMessage.error('Insufficient permissions, only admins can edit banners')
    return
  }

  isEditBanner.value = true
  bannerDialogTitle.value = 'Edit Banner'
  currentBanner.value = { ...banner }
  bannerDialogVisible.value = true
}

const saveBanner = async () => {
  if (!bannerFormRef.value) return

  try {
    const valid = await bannerFormRef.value.validate()
    if (!valid) return

    if (!currentBanner.value.imageUrl) {
      ElMessage.error('Please upload banner image first')
      return
    }

    bannerSaving.value = true

    let response
    if (isEditBanner.value) {
      response = await request.put(`/api/admin/banners/${currentBanner.value.id}`, currentBanner.value)
    } else {
      response = await request.post('/api/admin/banners', currentBanner.value)
    }

    if (response.code === 200) {
      ElMessage.success(isEditBanner.value ? 'Banner updated successfully' : 'Banner created successfully')
      bannerDialogVisible.value = false
      loadBanners()
    }
  } catch (error) {
    console.error('Failed to save banner:', error)
    ElMessage.error('Failed to save banner')
  } finally {
    bannerSaving.value = false
  }
}

const deleteBanner = async (banner) => {
  if (!userStore.isAdmin) {
    ElMessage.error('Insufficient permissions, only admins can delete banners')
    return
  }

  try {
    await ElMessageBox.confirm(`Are you sure to delete banner "${banner.title}"?`, 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    const response = await request.delete(`/api/admin/banners/${banner.id}`)

    if (response.code === 200) {
      ElMessage.success('Banner deleted successfully')
      loadBanners()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete banner:', error)
      ElMessage.error('Failed to delete banner')
    }
  }
}

// Image upload related methods
const handleImageUploadSuccess = (imageData) => {
  console.log('Image upload success:', imageData)
  ElMessage.success('Image uploaded successfully')
}

const handleImageUploadError = (error) => {
  console.error('Image upload failed:', error)
  ElMessage.error('Image upload failed, please try again')
}

const handleImageRemove = (file) => {
  currentBanner.value.imageUrl = ''
  ElMessage.info('Image removed')
}

// Category management methods
const showAddCategoryDialog = () => {
  isEditCategory.value = false
  categoryDialogTitle.value = 'Add Category'
  currentCategory.value = {
    id: null,
    name: '',
    description: '',
    sortOrder: 0,
    status: 1
  }
  categoryDialogVisible.value = true
}

const editCategory = (category) => {
  isEditCategory.value = true
  categoryDialogTitle.value = 'Edit Category'
  currentCategory.value = { ...category }
  categoryDialogVisible.value = true
}

const saveCategory = async () => {
  if (!categoryFormRef.value) return

  try {
    const valid = await categoryFormRef.value.validate()
    if (!valid) return

    categorySaving.value = true

    let response
    if (isEditCategory.value) {
      response = await request.put(`/api/admin/categories/${currentCategory.value.id}`, currentCategory.value)
    } else {
      response = await request.post('/api/admin/categories', currentCategory.value)
    }

    if (response.code === 200) {
      ElMessage.success(isEditCategory.value ? 'Category updated successfully' : 'Category created successfully')
      categoryDialogVisible.value = false
      loadCategories()
    }
  } catch (error) {
    console.error('Failed to save category:', error)
    ElMessage.error('Failed to save category')
  } finally {
    categorySaving.value = false
  }
}

const deleteCategory = async (category) => {
  try {
    await ElMessageBox.confirm(`Are you sure to delete category "${category.name}"?`, 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    const response = await request.delete(`/api/admin/categories/${category.id}`)

    if (response.code === 200) {
      ElMessage.success('Category deleted successfully')
      loadCategories()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete category:', error)
      ElMessage.error('Failed to delete category')
    }
  }
}

// Config management methods
const showAddConfigDialog = () => {
  isEditConfig.value = false
  configDialogTitle.value = 'Add Configuration'
  currentConfig.value = {
    id: null,
    configKey: '',
    configValue: '',
    configType: 'string',
    description: ''
  }
  configDialogVisible.value = true
}

const editConfig = (config) => {
  isEditConfig.value = true
  configDialogTitle.value = 'Edit Configuration'
  currentConfig.value = { ...config }
  configDialogVisible.value = true
}

const saveConfig = async () => {
  if (!configFormRef.value) return

  try {
    const valid = await configFormRef.value.validate()
    if (!valid) return

    configSaving.value = true

    const response = await request.post('/api/admin/system/configs', currentConfig.value)

    if (response.code === 200) {
      ElMessage.success('Configuration saved successfully')
      configDialogVisible.value = false
      loadAllConfigs()
      loadSystemConfigs()
    }
  } catch (error) {
    console.error('Failed to save config:', error)
    ElMessage.error('Failed to save config')
  } finally {
    configSaving.value = false
  }
}

const deleteConfig = async (config) => {
  try {
    await ElMessageBox.confirm(`Are you sure to delete config "${config.configKey}"?`, 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    const response = await request.delete(`/api/admin/system/configs/${config.configKey}`)

    if (response.code === 200) {
      ElMessage.success('Configuration deleted successfully')
      loadAllConfigs()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete config:', error)
      ElMessage.error('Failed to delete config')
    }
  }
}

// Notice management methods
const showAddNoticeDialog = () => {
  isEditNotice.value = false
  noticeDialogTitle.value = 'Publish Announcement'
  currentNotice.value = {
    id: null,
    title: '',
    content: '',
    type: 'notice',
    targetType: 'all',
    status: 1
  }
  noticeDialogVisible.value = true
}

const viewNotice = (notice) => {
  viewNoticeData.value = notice
  noticeViewDialogVisible.value = true
}

const editNotice = (notice) => {
  isEditNotice.value = true
  noticeDialogTitle.value = 'Edit Announcement'
  currentNotice.value = { ...notice }
  noticeDialogVisible.value = true
}

const saveNotice = async () => {
  if (!noticeFormRef.value) return

  try {
    const valid = await noticeFormRef.value.validate()
    if (!valid) return

    noticeSaving.value = true

    let response
    if (isEditNotice.value) {
      response = await request.put(`/api/admin/messages/${currentNotice.value.id}`, currentNotice.value)
    } else {
      response = await request.post('/api/admin/messages', currentNotice.value)
    }

    if (response.code === 200) {
      ElMessage.success(isEditNotice.value ? 'Announcement updated successfully' : 'Announcement published successfully')
      noticeDialogVisible.value = false
      loadNotices()
    }
  } catch (error) {
    console.error('Failed to save announcement:', error)
    ElMessage.error('Failed to save announcement')
  } finally {
    noticeSaving.value = false
  }
}

const handleNoticeCommand = async (command, notice) => {
  switch (command) {
    case 'edit':
      editNotice(notice)
      break
    case 'enable':
      await updateNoticeStatus(notice.id, 1)
      break
    case 'disable':
      await updateNoticeStatus(notice.id, 0)
      break
    case 'delete':
      await deleteNotice(notice)
      break
  }
}

const updateNoticeStatus = async (id, status) => {
  try {
    const response = await request.put(`/api/admin/messages/${id}/status`, null, {
      params: { status }
    })

    if (response.code === 200) {
      ElMessage.success('Status updated successfully')
      loadNotices()
    }
  } catch (error) {
    console.error('Failed to update status:', error)
    ElMessage.error('Failed to update status')
  }
}

const deleteNotice = async (notice) => {
  try {
    await ElMessageBox.confirm(`Are you sure to delete announcement "${notice.title}"?`, 'Confirm', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })

    const response = await request.delete(`/api/admin/messages/${notice.id}`)

    if (response.code === 200) {
      ElMessage.success('Announcement deleted successfully')
      loadNotices()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete announcement:', error)
      ElMessage.error('Failed to delete announcement')
    }
  }
}

const handleNoticePageSizeChange = (newSize) => {
  noticePagination.size = newSize
  noticePagination.page = 1
  loadNotices()
}

const handleNoticePageChange = (newPage) => {
  noticePagination.page = newPage
  loadNotices()
}

// Utility methods
const formatDateTime = (dateString) => {
  return dateString ? dayjs(dateString).format('MM-DD HH:mm') : '-'
}

const getNoticeTypeColor = (type) => {
  const colorMap = {
    system: 'info',
    activity: 'warning',
    notice: 'success'
  }
  return colorMap[type] || 'info'
}

const getNoticeTypeText = (type) => {
  const textMap = {
    system: 'System',
    activity: 'Activity',
    notice: 'Notice'
  }
  return textMap[type] || type
}

const getTargetTypeText = (type) => {
  const textMap = {
    all: 'All',
    user: 'Users',
    admin: 'Admins'
  }
  return textMap[type] || type
}

// Lifecycle
onMounted(() => {
  loadSystemConfigs()
  loadCategories()
  loadAllConfigs()
  loadBanners()
  loadNotices()
})
</script>

<style scoped>
/* Global Styles */
.system-config {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow-x: hidden;
  padding-bottom: 40px;
}

/* Ensure all elements have transparent background */
.system-config,
.system-config .page-header,
.system-config .header-content,
.system-config .header-left,
.system-config .page-title,
.system-config .page-subtitle,
.system-config .header-actions {
  background: transparent !important;
  background-color: transparent !important;
  background-image: none !important;
}

.page-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  pointer-events: none;
  min-height: 100vh;
}

.background-decoration {
  position: absolute;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 1200px;
  height: 1200px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  animation: float 12s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(-50%, -50%) translateY(0px) rotate(0deg); }
  50% { transform: translate(-50%, -50%) translateY(-40px) rotate(180deg); }
}

.background-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.05)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.05)"/><circle cx="50" cy="50" r="0.5" fill="rgba(255,255,255,0.03)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.6;
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
      180deg,
      transparent 0%,
      rgba(255, 255, 255, 0.02) 30%,
      rgba(255, 255, 255, 0.05) 100%
  );
}

/* Page Header */
.page-header {
  position: relative;
  z-index: 1;
  padding: 40px 24px;
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  max-width: 1400px;
  margin: 0 auto;
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 48px;
  font-weight: 900;
  margin: 0 0 12px 0;
  line-height: 1.1;
}

.title-gradient {
  background: linear-gradient(135deg, #ffffff 0%, rgba(255, 255, 255, 0.95) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3)) drop-shadow(0 0 20px rgba(0, 0, 0, 0.2));
}

.page-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  font-weight: 400;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}

.header-actions {
  display: flex;
  gap: 16px;
}

.create-button {
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: white;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.create-button:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.35);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.25);
}

/* Tabs Section */
.config-tabs-section {
  position: relative;
  margin: 0 24px 24px;
  z-index: 1;
}

.tabs-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.tabs-content {
  position: relative;
  padding: 0;
}

/* Modern Tabs */
.modern-tabs {
  border-radius: 20px;
  overflow: hidden;
}

.modern-tabs :deep(.el-tabs__header) {
  margin: 0;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-bottom: 1px solid #e2e8f0;
  padding: 0 32px;
}

.modern-tabs :deep(.el-tabs__nav-wrap) {
  background: transparent;
}

.modern-tabs :deep(.el-tabs__item) {
  padding: 20px 24px;
  font-weight: 600;
  color: #64748b;
  border: none;
  background: transparent;
  transition: all 0.3s ease;
}

.modern-tabs :deep(.el-tabs__item:hover) {
  color: #667eea;
}

.modern-tabs :deep(.el-tabs__item.is-active) {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 12px 12px 0 0;
}

.modern-tabs :deep(.el-tabs__content) {
  padding: 32px;
  background: transparent;
}

/* Config Section */
.config-section {
  width: 100%;
}

.config-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  transition: all 0.3s ease;
}

.config-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.12);
}

.card-header {
  padding: 24px 24px 16px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
}

.card-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 12px;
}

.card-icon {
  width: 24px;
  height: 24px;
  color: #667eea;
}

.card-content {
  padding: 24px;
}

/* Table Section */
.table-section {
  width: 100%;
}

.table-header {
  margin-bottom: 24px;
}

.results-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #1e293b;
}

.results-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.table-container {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  margin-bottom: 24px;
  overflow-x: auto;
}

/* Modern Table Styles - 精心控制表格宽度 */
.modern-table {
  border-radius: 0;
  min-width: 500px; /* 控制最小宽度 */
}

.modern-table :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.modern-table :deep(.el-table__header th) {
  background: transparent;
  border: none;
  padding: 16px 8px; /* 减小左右padding */
  font-weight: 600;
  color: #475569;
  font-size: 14px;
}

.modern-table :deep(.el-table__body tr) {
  transition: all 0.3s ease;
}

.modern-table :deep(.el-table__body tr:hover) {
  background: rgba(102, 126, 234, 0.05);
}

.modern-table :deep(.el-table__body td) {
  border: none;
  padding: 12px 8px; /* 减小左右padding */
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
}

/* Form Controls */
.modern-input :deep(.el-input__wrapper),
.modern-select :deep(.el-select__wrapper),
.modern-textarea :deep(.el-textarea__inner),
.modern-number-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-input :deep(.el-input__wrapper:hover),
.modern-select :deep(.el-select__wrapper:hover),
.modern-number-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.modern-input :deep(.el-input__wrapper.is-focus),
.modern-select :deep(.el-select__wrapper.is-focus),
.modern-number-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.modern-textarea :deep(.el-textarea__inner:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.modern-textarea :deep(.el-textarea__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.modern-switch :deep(.el-switch__core) {
  border-radius: 20px;
  background: #ddd;
  transition: all 0.3s ease;
}

.modern-switch :deep(.is-checked .el-switch__core) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.modern-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #374151;
  line-height: 1.5;
}

.upload-size-input {
  display: flex;
  align-items: center;
  gap: 12px;
}

.unit-text {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

/* Table Cell Styles */
.title-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.title-text {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
  line-height: 1.3;
}

.title-type {
  display: flex;
  align-items: center;
}

.mini-tag {
  font-size: 11px;
  padding: 2px 6px;
  height: auto;
  line-height: 1.2;
}

.image-preview {
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-image {
  border-radius: 6px;
  border: 1px solid #e2e8f0;
}

.no-image {
  width: 50px;
  height: 30px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  background: #f8fafc;
}

.link-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 13px;
}

.link-icon {
  font-size: 14px;
  color: #94a3b8;
}

.content-preview {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.4;
}

.content-icon {
  font-size: 14px;
  color: #94a3b8;
  margin-top: 2px;
  flex-shrink: 0;
}

.category-name {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1e293b;
  font-weight: 500;
}

.category-icon {
  font-size: 16px;
  color: #667eea;
}

.config-key {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1e293b;
  font-weight: 500;
}

.key-icon {
  font-size: 14px;
  color: #f59e0b;
}

.config-value {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
}

.type-tag {
  font-size: 11px;
  padding: 2px 6px;
}

.modern-status-tag {
  border-radius: 8px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  font-size: 12px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.date-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 12px;
}

.date-icon {
  font-size: 14px;
  color: #94a3b8;
}

.table-actions {
  display: flex;
  gap: 6px;
}

.action-btn {
  border-radius: 8px;
  padding: 6px;
  min-width: auto;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.more-btn {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #64748b;
}

.more-btn:hover {
  background: #e2e8f0;
  color: #475569;
}

/* Dropdown Styles */
.modern-dropdown {
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
  border: 1px solid #e2e8f0;
}

.modern-dropdown :deep(.el-dropdown-menu__item) {
  border-radius: 8px;
  padding: 8px 12px;
  margin: 2px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.modern-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.modern-dropdown :deep(.danger-item:hover) {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

/* Pagination */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.pagination-wrapper {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 50px;
  padding: 16px 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.modern-pagination :deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.modern-pagination :deep(.el-pager li) {
  border-radius: 8px;
  min-width: 36px;
  height: 36px;
  line-height: 36px;
  margin: 0 2px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-pagination :deep(.el-pager li:hover) {
  background: #667eea;
  color: white;
  transform: translateY(-1px);
}

.modern-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

/* Dialog Styles */
.modern-dialog {
  z-index: 2001;
}

.modern-dialog :deep(.el-overlay) {
  z-index: 2001;
  overflow: auto !important;
  padding: 20px;
  box-sizing: border-box;
}

.modern-dialog :deep(.el-overlay-dialog) {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  min-height: 100%;
  overflow: auto !important;
  padding: 20px 0;
}

.modern-dialog :deep(.el-dialog) {
  margin: 0 !important;
  max-height: none !important;
  height: auto !important;
  max-width: calc(100vw - 40px);
  width: 70%;
  min-width: 600px;
  border-radius: 20px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.modern-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  padding: 24px 32px;
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.modern-dialog :deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}

.modern-dialog :deep(.el-dialog__body) {
  flex: 1;
  overflow: hidden;
  padding: 0 !important;
  display: flex;
  flex-direction: column;
}

.modern-dialog :deep(.el-dialog__footer) {
  flex-shrink: 0;
}

.dialog-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 32px;
  max-height: 60vh;
  min-height: 200px;
}

.image-upload-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.upload-help-text {
  color: #64748b;
  font-size: 12px;
  line-height: 1.4;
}

.upload-help-text p {
  margin: 2px 0;
}

.modern-radio-group {
  display: flex;
  gap: 16px;
}

.modern-radio :deep(.el-radio__label) {
  font-weight: 500;
}

.notice-detail {
  width: 100%;
}

.modern-descriptions :deep(.el-descriptions__body) {
  background: #f8fafc;
  border-radius: 12px;
}

.modern-descriptions :deep(.el-descriptions__cell) {
  border-color: #e2e8f0;
}

.modern-descriptions :deep(.el-descriptions__label) {
  background: #f1f5f9;
  font-weight: 600;
  color: #475569;
}

.detail-tag {
  border-radius: 6px;
  font-weight: 500;
}

.notice-content {
  max-height: 200px;
  overflow-y: auto;
  line-height: 1.6;
  white-space: pre-wrap;
  color: #374151;
  background: #f9fafb;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.dialog-footer {
  padding: 24px 32px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.save-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  padding: 10px 24px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.save-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.cancel-button {
  border-radius: 12px;
  padding: 10px 24px;
  font-weight: 600;
  border: 1px solid #e2e8f0;
  background: white;
  color: #64748b;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.cancel-button:hover {
  border-color: #94a3b8;
  color: #475569;
  transform: translateY(-1px);
}

/* Responsive Design */
@media (max-width: 1400px) {
  :deep(.hidden-lg) {
    display: none !important;
  }
}

@media (max-width: 1200px) {
  :deep(.hidden-md) {
    display: none !important;
  }

  .modern-dialog :deep(.el-dialog) {
    width: 85%;
    min-width: 500px;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 20px 16px;
  }

  .header-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .page-title {
    font-size: 32px;
  }

  .page-subtitle {
    font-size: 16px;
  }

  .config-tabs-section {
    margin: 0 16px 16px;
  }

  .tabs-content {
    padding: 0;
  }

  .modern-tabs :deep(.el-tabs__content) {
    padding: 20px;
  }

  .modern-tabs :deep(.el-tabs__header) {
    padding: 0 20px;
  }

  .card-header,
  .card-content {
    padding: 16px;
  }

  .config-section .el-col {
    margin-bottom: 16px;
  }

  :deep(.hidden-sm) {
    display: none !important;
  }

  .modern-table {
    min-width: 420px; /* 减小移动端表格最小宽度 */
  }

  .modern-dialog :deep(.el-overlay) {
    padding: 10px;
  }

  .modern-dialog :deep(.el-dialog) {
    width: 95%;
    min-width: 320px;
    max-width: calc(100vw - 20px);
  }

  .dialog-content {
    padding: 20px;
    max-height: 50vh;
  }

  .pagination-wrapper {
    padding: 12px 16px;
  }

  .modern-pagination :deep(.el-pagination) {
    flex-wrap: wrap;
    justify-content: center;
  }
}

@media (max-width: 576px) {
  .page-title {
    font-size: 24px;
  }

  :deep(.hidden-xs) {
    display: none !important;
  }

  .table-actions {
    flex-direction: column;
    gap: 4px;
  }

  .action-btn {
    width: 26px;
    height: 26px;
    font-size: 12px;
  }

  .modern-table {
    min-width: 380px; /* 小屏幕最小宽度 */
  }

  .modern-dialog :deep(.el-overlay) {
    padding: 5px;
  }

  .modern-dialog :deep(.el-dialog) {
    width: 98%;
    min-width: 300px;
    max-width: calc(100vw - 10px);
  }

  .dialog-content {
    padding: 16px;
    max-height: 45vh;
  }

  .dialog-footer {
    padding: 16px 20px;
    flex-direction: column;
  }

  .dialog-footer .el-button {
    width: 100%;
  }

  .upload-size-input {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

/* Scrollbar Styles */
.table-container::-webkit-scrollbar {
  height: 8px;
}

.table-container::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

.table-container::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #cbd5e1 0%, #94a3b8 100%);
  border-radius: 4px;
}

.table-container::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
}

.dialog-content::-webkit-scrollbar,
.notice-content::-webkit-scrollbar {
  width: 6px;
}

.dialog-content::-webkit-scrollbar-track,
.notice-content::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.dialog-content::-webkit-scrollbar-thumb,
.notice-content::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #cbd5e1 0%, #94a3b8 100%);
  border-radius: 3px;
}

/* Global scroll control */
body.dialog-open {
  overflow: auto !important;
}

html.dialog-open {
  overflow: auto !important;
}

.el-popup-parent--hidden {
  overflow: auto !important;
}

/* Loading and Animation States */
.system-config {
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.config-tabs-section {
  animation: slideUp 0.6s ease-out;
  animation-delay: 0.1s;
  animation-fill-mode: both;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Hover Effects */
.config-tabs-section:hover .tabs-background {
  box-shadow: 0 16px 35px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.tabs-background {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
</style>