const mongoose = require('mongoose');

const imageLinksSchema = new mongoose.Schema({
    smallThumbnail: {
        type: String,
        default: null
    },
    thumbnail: {
        type: String,
        default: null
    }
});

const volumeInfoSchema = new mongoose.Schema({
    title: {
        type: String,
        required: true,
        trim: true
    },
    authors: [{
        type: String,
        required: true
    }],
    description: {
        type: String,
        default: null
    },
    publisher: {
        type: String,
        default: null
    },
    publishedDate: {
        type: String,
        default: null
    },
    pageCount: {
        type: Number,
        default: null
    },
    imageLinks: {
        type: imageLinksSchema,
        default: null
    },
    language: {
        type: String,
        default: null
    }
});

const bookSchema = new mongoose.Schema({
    id: {
        type: String,
        required: true,
        unique: true
    },
    volumeInfo: {
        type: volumeInfoSchema,
        required: true
    }
});

bookSchema.set('toJSON', {
  transform: (doc, ret) => {
      delete ret._id; // Remove book-level _id
      if (ret.volumeInfo) {
          delete ret.volumeInfo._id; // Remove _id from volumeInfo
          if (ret.volumeInfo.imageLinks) {
              delete ret.volumeInfo.imageLinks._id; // Remove _id from imageLinks
          }
      }
      return ret;
  }
});


const userSchema = new mongoose.Schema({
  username: {
    type: String,
    required: true,
    unique: true,
    trim: true,
    minlength: 3
  },
  email: {
    type: String,
    required: true,
    unique: true,
    trim: true,
    lowercase: true,
    match: [/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/, 'Please enter a valid email']
  },
  password: {
    type: String,
    required: true,
    minlength: 6
  },
  profileImage: {
    type: String,
    default: null
  },
  createdAt: {
    type: Date,
    default: Date.now
  },
  favoriteBooks: {
    type: [bookSchema], default: []
  },
  currentlyReading: {
    type: [bookSchema], default: []
  },
  wantToRead: {
    type: [bookSchema], default: []
  },
  read: {
    type: [bookSchema],  default: []
  },
  preferences: {
    favoriteGenres: [String],
    emailNotifications: {
      type: Boolean,
      default: true
    },
    darkMode: {
      type: Boolean,
      default: false
    }
  }
});

// Add an index for email and username to improve query performance
userSchema.index({ email: 1, username: 1 });

module.exports = mongoose.model('User', userSchema);