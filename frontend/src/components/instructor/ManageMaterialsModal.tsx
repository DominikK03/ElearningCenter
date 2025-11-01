import { useState } from 'react';
import { Upload, Trash2, FileText, Image as ImageIcon, FileIcon, Link as LinkIcon } from 'lucide-react';
import toast from 'react-hot-toast';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import ConfirmDialog from '../ui/ConfirmDialog';
import { uploadMaterial, addLinkMaterial, deleteMaterial } from '../../services/materialService';
import type { Lesson, Material } from '../../types/api';

interface ManageMaterialsModalProps {
  isOpen: boolean;
  onClose: () => void;
  lesson: Lesson;
  courseId: number;
  sectionId: number;
  onRefresh: () => Promise<void>;
}

const ALLOWED_FILE_TYPES = [
  'image/jpeg',
  'image/jpg',
  'image/png',
  'image/gif',
  'image/webp',
  'application/pdf',
];

const ALLOWED_EXTENSIONS = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.pdf'];

type UploadMode = 'file' | 'link';

export default function ManageMaterialsModal({
  isOpen,
  onClose,
  lesson,
  courseId,
  sectionId,
  onRefresh,
}: ManageMaterialsModalProps) {
  const [uploadMode, setUploadMode] = useState<UploadMode>('file');
  const [title, setTitle] = useState('');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [linkUrl, setLinkUrl] = useState('');
  const [isUploading, setIsUploading] = useState(false);
  const [isDeletingId, setIsDeletingId] = useState<number | null>(null);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [materialToDelete, setMaterialToDelete] = useState<Material | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];

      // Validate file type
      if (!ALLOWED_FILE_TYPES.includes(file.type)) {
        toast.error('Invalid file type. Only images (JPG, PNG, GIF, WEBP) and PDF files are allowed.');
        e.target.value = '';
        return;
      }

      // Validate file size (10MB max)
      const maxSize = 10 * 1024 * 1024; // 10MB
      if (file.size > maxSize) {
        toast.error('File is too large. Maximum size is 10MB.');
        e.target.value = '';
        return;
      }

      setSelectedFile(file);
    }
  };

  const handleUpload = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!title.trim()) {
      toast.error('Please enter a title');
      return;
    }

    try {
      setIsUploading(true);

      if (uploadMode === 'file') {
        if (!selectedFile) {
          toast.error('Please select a file');
          setIsUploading(false);
          return;
        }
        await uploadMaterial(courseId, sectionId, lesson.id, title.trim(), selectedFile);
        toast.success('Material uploaded successfully!');
      } else {
        if (!linkUrl.trim()) {
          toast.error('Please enter a URL');
          setIsUploading(false);
          return;
        }
        await addLinkMaterial(courseId, sectionId, lesson.id, title.trim(), linkUrl.trim());
        toast.success('Link material added successfully!');
      }

      // Reset form
      setTitle('');
      setSelectedFile(null);
      setLinkUrl('');
      const fileInput = document.getElementById('file-input') as HTMLInputElement;
      if (fileInput) fileInput.value = '';

      await onRefresh();
    } catch (err: any) {
      console.error('Error uploading material:', err);
      toast.error(err.response?.data?.message || 'Failed to upload material');
    } finally {
      setIsUploading(false);
    }
  };

  const openDeleteDialog = (material: Material) => {
    setMaterialToDelete(material);
    setShowDeleteDialog(true);
  };

  const handleDeleteConfirm = async () => {
    if (!materialToDelete) return;

    try {
      setIsDeletingId(materialToDelete.id);
      await deleteMaterial(courseId, sectionId, lesson.id, materialToDelete.id);
      toast.success('Material deleted successfully!');
      setShowDeleteDialog(false);
      setMaterialToDelete(null);
      await onRefresh();
    } catch (err: any) {
      console.error('Error deleting material:', err);
      toast.error(err.response?.data?.message || 'Failed to delete material');
    } finally {
      setIsDeletingId(null);
    }
  };

  const getMaterialIcon = (fileType: string) => {
    if (fileType === 'LINK') {
      return <LinkIcon className="w-5 h-5 text-green-400" />;
    } else if (fileType === 'IMAGE' || fileType.startsWith('image/')) {
      return <ImageIcon className="w-5 h-5 text-blue-400" />;
    } else if (fileType === 'PDF' || fileType === 'application/pdf') {
      return <FileText className="w-5 h-5 text-red-400" />;
    }
    return <FileIcon className="w-5 h-5 text-gray-400" />;
  };

  const handleClose = () => {
    setTitle('');
    setSelectedFile(null);
    setLinkUrl('');
    setUploadMode('file');
    const fileInput = document.getElementById('file-input') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={`Manage Materials - ${lesson.title}`}
      size="lg"
    >
      <div className="space-y-6">
        {/* Upload Form */}
        <div className="bg-gray-800 border border-gray-700 rounded-lg p-6">
          <h3 className="text-lg font-semibold text-gray-100 mb-4">
            Add New Material
          </h3>

          {/* Tabs */}
          <div className="flex gap-2 mb-4">
            <button
              type="button"
              onClick={() => setUploadMode('file')}
              className={`flex-1 px-4 py-2 rounded-lg font-medium transition-colors ${
                uploadMode === 'file'
                  ? 'bg-purple-600 text-white'
                  : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
              }`}
            >
              <Upload className="w-4 h-4 inline-block mr-2" />
              Upload File
            </button>
            <button
              type="button"
              onClick={() => setUploadMode('link')}
              className={`flex-1 px-4 py-2 rounded-lg font-medium transition-colors ${
                uploadMode === 'link'
                  ? 'bg-purple-600 text-white'
                  : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
              }`}
            >
              <LinkIcon className="w-4 h-4 inline-block mr-2" />
              Add Link
            </button>
          </div>

          <form onSubmit={handleUpload} className="space-y-4">
            <div>
              <label htmlFor="material-title" className="block text-sm font-medium text-gray-300 mb-2">
                Title
              </label>
              <input
                type="text"
                id="material-title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="e.g., Course Slides, Exercise PDF"
                className="w-full px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500"
                disabled={isUploading}
              />
            </div>

            {uploadMode === 'file' ? (
              <>
                <div>
                  <label htmlFor="file-input" className="block text-sm font-medium text-gray-300 mb-2">
                    File
                  </label>
                  <input
                    type="file"
                    id="file-input"
                    onChange={handleFileChange}
                    accept={ALLOWED_EXTENSIONS.join(',')}
                    className="w-full px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:bg-purple-600 file:text-white file:cursor-pointer hover:file:bg-purple-700"
                    disabled={isUploading}
                  />
                  <p className="text-xs text-gray-400 mt-2">
                    Allowed: Images (JPG, PNG, GIF, WEBP) and PDF. Max size: 10MB
                  </p>
                </div>

                {selectedFile && (
                  <div className="bg-gray-900 rounded-lg p-3 border border-gray-700">
                    <p className="text-sm text-gray-300">
                      Selected: <span className="font-medium">{selectedFile.name}</span>
                    </p>
                    <p className="text-xs text-gray-400 mt-1">
                      Size: {(selectedFile.size / 1024).toFixed(2)} KB
                    </p>
                  </div>
                )}
              </>
            ) : (
              <div>
                <label htmlFor="link-url" className="block text-sm font-medium text-gray-300 mb-2">
                  URL
                </label>
                <input
                  type="url"
                  id="link-url"
                  value={linkUrl}
                  onChange={(e) => setLinkUrl(e.target.value)}
                  placeholder="https://example.com/resource"
                  className="w-full px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  disabled={isUploading}
                />
                <p className="text-xs text-gray-400 mt-2">
                  Enter a valid URL to an external resource
                </p>
              </div>
            )}

            <div className="flex justify-end gap-3">
              <Button
                type="button"
                variant="secondary"
                onClick={handleClose}
                disabled={isUploading}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="primary"
                disabled={
                  isUploading ||
                  !title.trim() ||
                  (uploadMode === 'file' ? !selectedFile : !linkUrl.trim())
                }
              >
                {isUploading
                  ? uploadMode === 'file'
                    ? 'Uploading...'
                    : 'Adding...'
                  : uploadMode === 'file'
                  ? 'Upload Material'
                  : 'Add Link'}
              </Button>
            </div>
          </form>
        </div>

        {/* Existing Materials List */}
        <div>
          <h3 className="text-lg font-semibold text-gray-100 mb-4">
            Existing Materials ({lesson.materials.length})
          </h3>

          {lesson.materials.length === 0 ? (
            <div className="bg-gray-800 border border-gray-700 rounded-lg p-8 text-center">
              <FileText className="w-12 h-12 text-gray-600 mx-auto mb-3" />
              <p className="text-gray-400">No materials uploaded yet</p>
            </div>
          ) : (
            <div className="space-y-3">
              {lesson.materials.map((material) => (
                <div
                  key={material.id}
                  className="bg-gray-800 border border-gray-700 rounded-lg p-4 flex items-center justify-between hover:border-gray-600 transition-colors"
                >
                  <div className="flex items-center gap-3 flex-1">
                    {getMaterialIcon(material.fileType)}
                    <div className="flex-1">
                      <h4 className="text-gray-100 font-medium">{material.title}</h4>
                      <p className="text-sm text-gray-400">
                        {material.fileType === 'LINK'
                          ? 'External Link'
                          : material.fileType === 'PDF' || material.fileType === 'application/pdf'
                          ? 'PDF Document'
                          : material.fileType === 'IMAGE' || material.fileType.startsWith('image/')
                          ? 'Image'
                          : 'Document'}
                      </p>
                    </div>
                    <a
                      href={material.fileUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-purple-400 hover:text-purple-300 text-sm font-medium"
                    >
                      {material.fileType === 'LINK' ? 'Open' : 'View'}
                    </a>
                  </div>

                  <Button
                    variant="ghost"
                    onClick={() => openDeleteDialog(material)}
                    disabled={isDeletingId === material.id}
                    className="!p-2 !text-red-400 hover:!text-red-300 ml-3"
                    title="Delete material"
                  >
                    {isDeletingId === material.id ? (
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-red-400"></div>
                    ) : (
                      <Trash2 className="w-4 h-4" />
                    )}
                  </Button>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Delete Material Confirmation Dialog */}
      <ConfirmDialog
        isOpen={showDeleteDialog}
        onClose={() => {
          setShowDeleteDialog(false);
          setMaterialToDelete(null);
        }}
        onConfirm={handleDeleteConfirm}
        title="Delete Material"
        message={`Are you sure you want to delete "${materialToDelete?.title}"? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        variant="danger"
        isLoading={isDeletingId === materialToDelete?.id}
      />
    </Modal>
  );
}
